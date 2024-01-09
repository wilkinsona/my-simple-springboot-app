package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

@Slf4j
@Service
public class ContextPropagationService {
    private final WebClient webClient;

    public ContextPropagationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:47923")
            .build();
    }

    public void testBlock() {
        log.info("[testBlock] started");
        baseMethod(this::httpCall)
            .onErrorResume(e -> Mono.empty())
            .doOnSuccess(resp -> log.info("[testBlock] doOnSuccess triggered with resp={}", resp))
            .block();
    }

    public void testSubscribeOnBoundedElasticWithoutWebClient() {
        log.info("[testSubscribeOnBoundedElasticWithoutWebClient] started");
        baseMethod(this::someWorkWithoutWebClient)
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(e -> Mono.empty())
            .doOnSuccess(resp ->
                log.info("[testSubscribeOnBoundedElasticWithoutWebClient] doOnSuccess triggered with resp={}", resp))
            .subscribe();
    }

    public void testSubscribeWithReactiveWebClient() {
        log.info("[testSubscribeWithReactiveWebClient] started");
        baseMethod(this::httpCall)
            .onErrorResume(e -> Mono.empty())
            .doOnSuccess(resp -> log.info("[testSubscribeWithReactiveWebClient] doOnSuccess triggered with resp={}",
                resp))
            .subscribe();
    }

    private Mono<String> baseMethod(Function<String, Mono<? extends String>> someWork) {
        return Mono.fromSupplier(() -> "test")
            .doOnNext(value -> log.info("[baseMethod] doOnNext triggered with value={}", value))
            .flatMap(someWork)
            .doOnSuccess(resp -> log.info("[baseMethod] doOnSuccess triggered with resp={}", resp))
            .doOnError(e -> log.error("[baseMethod] doOnError triggered", e));
    }

    private Mono<String> httpCall(String value) {
        log.info("[httpCall] started with value={}", value);
        return webClient
            .get()
            .uri("/test")
            .retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(resp -> log.info("[httpCall] doOnSuccess triggered with resp={}", resp))
            .doOnError(e -> log.error("[httpCall] doOnError triggered with exception", e));
    }

    private Mono<String> someWorkWithoutWebClient(String value) {
        return Mono.fromSupplier(() -> value)
            .doOnSuccess(resp -> log.info("[httpCall] doOnSuccess triggered with resp={}", resp))
            .doOnError(e -> log.error("[httpCall] doOnError triggered with exception", e));
    }
}
