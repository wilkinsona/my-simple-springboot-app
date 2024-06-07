# How to Run
```shell
# spin up required containers
docker-compose up --build --force-recreate app

# test APIs
curl -v localhost:7080/hget-reactive

curl -v localhost:7080/hset-reactive
curl -v localhost:7080/hget-reactive

```

# Thread Dump
## Use CLI
```shell
docker-compose exec app jcmd 6 Thread.print > threaddump.txt
```
## Use Actuator Endpoint
```shell
curl 'http://localhost:7081/actuator/threaddump' -i -X GET -H 'Accept: text/plain'
```

# Bug
Application hangs on startup after upgrading to Spring Boot 3.2.x.

## Steps to Reproduce
- run application using docker

## Expected behaviour
- application should start and able to serve traffic

## Actual Behaviour
- observe that application hangs indefinitely for the creation of `ReactiveRedisMessageListenerContainer` which is seemingly waiting for `ReactiveRedisConnectionFactory` to provision `ReactiveConnection`

## Related Issues
- https://github.com/spring-projects/spring-boot/issues/39240
- https://github.com/spring-projects/spring-data-redis/issues/2814

## Known Workarounds
1. use `@Lazy` for lazy initialization of `ReactiveRedisMessageListenerContainer`
2. manually inject `MicrometerOptions` as a bean the same way as how LettuceMetricsAutoConfiguration does it
