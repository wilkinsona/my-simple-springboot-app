# How to Run
```shell
./gradlew bootRun
```

# Thread Dump
## Use CLI
```shell
kill -3 <pid>
```

# Bug
Application hangs on startup after upgrading to Spring Boot 3.2.x.

## Steps to Reproduce
- Run application

## Expected behaviour
- Application should start and able to serve traffic

## Actual Behaviour
- Observe that application hangs indefinitely for the creation of `ReactiveRedisMessageListenerContainer` which is seemingly waiting for `ReactiveRedisConnectionFactory` to provision `ReactiveConnection`
- [thread dump captured](./threaddump.txt)

## Related Issues
- https://github.com/spring-projects/spring-boot/issues/39240
- https://github.com/spring-projects/spring-data-redis/issues/2814

## Known Workarounds
1. use `@Lazy` for lazy initialization of `ReactiveRedisMessageListenerContainer`
2. manually inject `MicrometerOptions` as a bean the same way as how LettuceMetricsAutoConfiguration does it
