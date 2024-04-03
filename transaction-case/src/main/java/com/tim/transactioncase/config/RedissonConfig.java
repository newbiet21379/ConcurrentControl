package com.tim.transactioncase.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    private final ApplicationProperties applicationProperties;
    public RedissonConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + applicationProperties.getRedis().getHost()
                + ":" + applicationProperties.getRedis().getPort()).setUsername(null).setPassword(applicationProperties.getRedis().getPassword());
        return Redisson.create(config);
    }
}
