package com.example.demo;

import org.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

@Configuration
public class RedisConfig {
    Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    // Lazy initialization allows application to start and function properly
    //    @Lazy
    @Bean
    ReactiveRedisMessageListenerContainer redisMessageListenerContainer(
        ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        logger.info("=== ReactiveRedisMessageListenerContainer");
        return new ReactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory);
    }

    /** Another Workaround for Micrometer-Redis Bug
     *
     * See https://github.com/spring-projects/spring-data-redis/issues/2814
     *
     * @return Micrometer options
     */
    //    @Bean
    //    MicrometerOptions micrometerOptions(Tracer tracer) {
    //        return MicrometerOptions.create();
    //    }

}
