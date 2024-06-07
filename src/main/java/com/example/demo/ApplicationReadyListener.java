package com.example.demo;

import org.slf4j.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

//@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(ApplicationReadyListener.class);
    private final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    private final GenericApplicationContext genericApplicationContext;

    public ApplicationReadyListener(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, GenericApplicationContext genericApplicationContext) {
        this.reactiveRedisConnectionFactory = reactiveRedisConnectionFactory;
        this.genericApplicationContext = genericApplicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Application ready - ApplicationReadyEvent");
        ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer =
            new ReactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory);
        genericApplicationContext.registerBean(ReactiveRedisMessageListenerContainer.class,
            reactiveRedisMessageListenerContainer);
    }
}
