package com.onion.backend.batchjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class AdViewCountJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job dailyAdViewUniqueCountJob;

    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
    public void runDailyAdViewCountJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("광고 조회수 집계 배치 작업 시작");
            jobLauncher.run(dailyAdViewUniqueCountJob, jobParameters);
            log.info("광고 조회수 집계 배치 작업 완료");
        } catch (Exception e) {
            log.error("광고 조회수 집계 배치 작업 실패: {}", e.getMessage(), e);
        }
    }
}
