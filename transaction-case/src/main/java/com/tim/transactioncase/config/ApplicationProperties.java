package com.tim.transactioncase.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data")
@Data
public class ApplicationProperties {
    private Redis redis = new Redis();

    @Data
    public static class Redis {
        private String host;
        private String port;
        private String password;
    }
}
