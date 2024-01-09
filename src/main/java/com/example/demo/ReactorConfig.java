package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
@Slf4j
public class ReactorConfig {

    @PostConstruct
    void configureLifeCycleHooks() {
        Hooks.enableAutomaticContextPropagation();
    }
}
