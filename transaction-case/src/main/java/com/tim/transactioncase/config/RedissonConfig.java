package com.tim.transactioncase.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedissonConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedissonConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedissonClient redissonClient() {
        RedisStandaloneConfiguration standaloneConfig = ((LettuceConnectionFactory) redisConnectionFactory)
                .getStandaloneConfiguration();
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + standaloneConfig.getHostName()
                + ":" + standaloneConfig.getPort()).setPassword(String.valueOf(standaloneConfig.getPassword()));
        return Redisson.create(config);
    }
}
