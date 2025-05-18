package com.onion.backend.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Getter
@Setter
public class ElasticSearchProperties {

    private String host;
    private int port;
    private String username;
    private String password;

    private int connectTimeout;
    private int socketTimeout;
    private int maxConnection;
    private int maxConnectionPerRoute;

    private final String articleIndex = "article";


}
