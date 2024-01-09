package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ContextPropagationController {
    private final ContextPropagationService contextPropagationService;

    public ContextPropagationController(ContextPropagationService contextPropagationService) {
        this.contextPropagationService = contextPropagationService;
    }

    @GetMapping("/test-context-propagation-block")
    ResponseEntity<Object> testContextPropagationBlock() {
        log.info("/test-context-propagation-block called");
        contextPropagationService.testBlock();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test-context-propagation-subscribe-with-reactive-web-client")
    ResponseEntity<Object> testContextPropagationSubscribe() {
        log.info("/test-context-propagation-subscribe-with-reactive-web-client called");
        contextPropagationService.testSubscribeWithReactiveWebClient();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test-context-propagation-subscribe-without-webclient")
    ResponseEntity<Object> testContextPropagationSubscribeBoundedElastic() {
        log.info("/test-context-propagation-subscribe-without-webclient called");
        contextPropagationService.testSubscribeOnBoundedElasticWithoutWebClient();
        return ResponseEntity.ok().build();
    }
}
