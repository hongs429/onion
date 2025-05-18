package com.onion.backend.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class ElasticSearchConfig {

    private final ElasticSearchProperties elasticSearchProperties;


    @Bean
    public ElasticsearchClient elasticsearchClient() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestClientTransport transport = new RestClientTransport(
                restClient(),
                new JacksonJsonpMapper(mapper)
        );

        return new ElasticsearchClient(transport);
    }


    @Bean
    public RestClient restClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(elasticSearchProperties.getHost(), elasticSearchProperties.getPort())
        );

        if (elasticSearchProperties.getUsername() != null && elasticSearchProperties.getPassword() != null) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider
                    .setCredentials(
                            AuthScope.ANY,
                            new UsernamePasswordCredentials(elasticSearchProperties.getUsername(),
                                    elasticSearchProperties.getPassword())
                    );
            builder.setHttpClientConfigCallback(
                    httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );
        }

        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(elasticSearchProperties.getConnectTimeout())
                        .setSocketTimeout(elasticSearchProperties.getSocketTimeout())
        );

        // Cluster 모드, Standalone 모드 일 경우에 맞게 세팅해야한다.
        // maxConnection : 인스턴스 한대가 ElasticSearch 엔진에 붙을 수 있는 최대 커넥션
        // maxConnectionPerRoute : ElasticSearch의 데이터 노드 한대에 붙는 최대 커넥션 갯수.
        // 예시
        // 서버 3대, 데이터 노드 5개, maxConn 150, maxConnPerRoute 30
        // -> 각 서버는 150개의 커넥션을 각 데이터 노드에 30개씩 할당. 30(perRoute) * 5(data nodes) = 150
        // -> 각 데이터 노드는 3개의 서버로부터 30개씩의 커넥션을 맺음. 각 데이터 노드는 90개의 커넥션을 가지고 있음.
        // 해당 설정은 Cluster, Standalone 모드 모두에 대해서 동일하게 적용해야함.
        // 때문에 ElasticSearch의 모드 별로 위의 계산을 비추어 적절한 값을 세팅해주어야 함.
        builder.setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder
                        .setMaxConnTotal(elasticSearchProperties.getMaxConnection())
                        .setMaxConnPerRoute(elasticSearchProperties.getMaxConnectionPerRoute())
        );

        return builder.build();
    }


}
