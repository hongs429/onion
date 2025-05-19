package com.onion.backend.batchjob.job;

import com.mongodb.client.MongoCursor;
import com.onion.backend.batchjob.dto.DailyAdViewStatisticsDTO;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdViewCountJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MongoTemplate mongoTemplate;
    private final DataSource dataSource;

    private static final String TEMP_COLLECTION_NAME = "daily_ad_view_statistics_temp";
    private static final int CHUNK_SIZE = 500;

    @Bean
    public Job dailyAdViewUniqueCountJob() {
        return new JobBuilder("dailyAdViewUniqueCountJob", jobRepository)
                .start(prepareTempCollectionStep())
                .next(aggregateToTempCollectionStep())
                .next(saveToDatabaseStep())
                .build();
    }

    @Bean
    public Step prepareTempCollectionStep() {
        return new StepBuilder("prepareTempCollectionStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate targetDate = LocalDate.now().minusDays(1);
                    log.info("임시 컬렉션 준비 시작: {}", targetDate);

                    if (mongoTemplate.collectionExists(TEMP_COLLECTION_NAME)) {
                        mongoTemplate.getCollection(TEMP_COLLECTION_NAME).deleteMany(new Document());
                        log.info("기존 임시 컬랙션 데이터 삭제 완료");
                    } else {
                        mongoTemplate.createCollection(TEMP_COLLECTION_NAME);
                        log.info("임시 컬레션 생선 완료");

                        mongoTemplate.getCollection(TEMP_COLLECTION_NAME)
                                .createIndex(new Document("advertisementId", 1));
                        log.info("임시 컬랙션 인덱스 생성 완료");
                    }

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();

    }

    @Bean
    public Step aggregateToTempCollectionStep() {
        return new StepBuilder("aggregateToTempCollectionStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    LocalDate targetDate = LocalDate.now().minusDays(1);
                    LocalDateTime startOfDay = targetDate.atStartOfDay();
                    LocalDateTime endOfDay = LocalDate.now().atStartOfDay();

                    log.info("MongoDB 대용량 광고 조회 집계 시작: {}", targetDate);

                    List<Document> aggregationPipeline = Arrays.asList(
                            // 1. 날짜 필터링 - 인덱스 활용
                            new Document("$match",
                                    new Document("createDateTime",
                                            new Document("$gte", startOfDay)
                                                    .append("$lt", endOfDay)
                                    )
                            ),

                            // 2. 필요한 필드만 추출 - 메모리 사용 최소화
                            new Document("$project", new Document("advertisementId", 1)
                                    .append("userIdentifier",
                                            new Document("$ifNull", List.of("$userId", "$ip"))
                                    )
                            ),

                            // 3. 광고id + 사용자 기준으로 그룹화 - 유니크 사용자 계산
                            new Document("$group",
                                    new Document("_id",
                                            new Document("adId", "$advertisementId")
                                                    .append("userId", "$userIdentifier")
                                    )
                            ),

                            // 4. 광고Id를 기준으로 다시 그룹하여 카운트
                            new Document("$group",
                                    new Document("_id", "$_id.adId")
                                            .append("advertisementId", new Document("$first", "$_id.adId")) // 누산기 함수 사용
                                            .append("uniqueViewCount", new Document("$sum", 1))
                                            .append("statisticsDate", new Document("$first", targetDate))
                            ),

                            // 5. 결과를 임시 컬렉션에 저장
                            new Document("$out", TEMP_COLLECTION_NAME)
                    );

                    Document command = new Document("aggregate", "adViewHistory")
                            .append("pipeline", aggregationPipeline)
                            .append("cursor", new Document("batchSize", 1000))  // batchSize 추가
                            .append("allowDiskUse", true);// 메모리 제한 우회. 집계 연산 중 메모리 제한(100MB)을 초과할 경우 디스크를 사용할 수 있도록 허용

                    log.info("MongoDB 집계 쿼리 실행");

                    // 집계 쿼리 실행
                    Document result = mongoTemplate.getDb().runCommand(command);

                    // 집계 결과 확인
                    long count = mongoTemplate.getCollection(TEMP_COLLECTION_NAME).countDocuments();
                    log.info("MongoDB 집계 완료: {} 건의 광고 통계 데이터 생성", count);

                    // 작업 컨텍스트에 상태 저장 (다음 작업에서 활용)
                    contribution.getStepExecution().getExecutionContext()
                            .putString("tempCollectionName", TEMP_COLLECTION_NAME);
                    contribution.getStepExecution().getExecutionContext()
                            .putString("targetDate", targetDate.toString());

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step saveToDatabaseStep() {
        return new StepBuilder("saveToDatabaseStep", jobRepository)
                .<Document, DailyAdViewStatisticsDTO>chunk(CHUNK_SIZE, transactionManager)
                .reader(mongoTempCollectionReader())
                .processor(documentToEntityProcessor())
                .writer(mysqlItemWriter())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        LocalDate targetDate = LocalDate.parse(
                                stepExecution.getExecutionContext()
                                        .getString(
                                                "targetDate",
                                                LocalDate.now().minusDays(1).toString()
                                        )
                        );

                        log.info("MySQL 기존 데이터 삭제 시작: {}", targetDate);

                        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                        int deletedCount = jdbcTemplate.update(
                                "DELETE FROM daily_ad_view_statistics where statistics_date = ?",
                                Date.valueOf(targetDate)
                        );

                        log.info("MySQL 기존 데이터 삭제 완료: {} 건", deletedCount);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.info("MySQL 저장 완료: {} 건 처리됨",
                                stepExecution.getWriteCount());
                        return null;
                    }
                })
                .build();
    }


    @Bean
    public ItemReader<Document> mongoTempCollectionReader() {
        return new ItemReader<Document>() {
            private MongoCursor<Document> cursor;

            private final AtomicInteger count = new AtomicInteger(0);
            private boolean initialized = false;

            @Override
            public Document read()
                    throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (!initialized) {
                    initialized = true;
                    log.info("MongoDB 임시 컬렉션에서 데이터 읽기 시작");

                    cursor = mongoTemplate.getCollection(TEMP_COLLECTION_NAME)
                            .find()
                            .batchSize(CHUNK_SIZE)
                            .iterator();
                }

                if (cursor != null && cursor.hasNext()) {
                    Document document = cursor.next();
                    int processed = count.incrementAndGet();

                    if (processed % 1000 == 0) {
                        log.info("{} 건 처리 중...", processed);
                    }

                    return document;
                } else {
                    if (cursor != null) {
                        cursor.close();
                    }
                    log.info("MongoDB 임시 컬렉션 읽기 완료: 총 {} 건", count.get());
                    return null;
                }
            }
        };

    }

    @Bean
    public ItemProcessor<Document, DailyAdViewStatisticsDTO> documentToEntityProcessor() {
        return document -> {
            String advertisementId = document.getString("advertisementId");
            long uniqueViewCount = document.get("uniqueViewCount", Number.class).longValue();
            LocalDate statisticsDate;
            Object dateObj = document.get("statisticsDate");
            if (dateObj instanceof Date) {
                statisticsDate = ((Date) dateObj).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
            } else {
                statisticsDate = LocalDate.now().minusDays(1);
            }

            // 날짜가 null인 경우 기본값 설정
            if (statisticsDate == null) {
                statisticsDate = LocalDate.now().minusDays(1);
            }
            return DailyAdViewStatisticsDTO.builder()
                    .dailyAdViewStatisticsId(UUID.randomUUID())
                    .advertisementId(UUID.fromString(advertisementId))
                    .uniqueViewCount(uniqueViewCount)
                    .statisticsDate(statisticsDate)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

        };

    }

    @Bean
    public ItemWriter<DailyAdViewStatisticsDTO> mysqlItemWriter() {
        JdbcBatchItemWriter<DailyAdViewStatisticsDTO> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO daily_ad_view_statistics " +
                "(daily_ad_view_statistics_id, advertisement_id, unique_view_count, statistics_date, created_at, updated_at) " +
                "VALUES (uuid_to_bin(?), uuid_to_bin(?), ?, ?, ?, ?)");

        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setString(1, item.getDailyAdViewStatisticsId().toString());
            ps.setString(2, item.getAdvertisementId().toString());
            ps.setLong(3, item.getUniqueViewCount());
            ps.setDate(4, java.sql.Date.valueOf(item.getStatisticsDate()));
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(item.getCreatedAt()));
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(item.getUpdatedAt()));
        });

        writer.afterPropertiesSet();
        return writer;
    }
}
