package com.example.demo;

import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BeanInitializationLogger implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BeanInitializationLogger.class);
    private static final List<String> beans = new LinkedList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Log before initialization (can also use postProcessAfterInitialization if preferred)
        logger.info("Initializing bean: {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        beans.add(bean.getClass().getSimpleName());
        logger.info("Initialized beans: {}", beans);
        return bean;
    }
}
