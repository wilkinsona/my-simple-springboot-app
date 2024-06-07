package com.example.demo;

import org.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

@Configuration
public class RedisConfig {
    Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    // Commenting out this allows application to start and reactive redis client still works
    @Bean
    ReactiveRedisMessageListenerContainer redisMessageListenerContainer(
        ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        logger.info("=== ReactiveRedisMessageListenerContainer");
        return new ReactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory);
    }

}
