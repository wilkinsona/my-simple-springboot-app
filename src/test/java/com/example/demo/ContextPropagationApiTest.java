package com.example.demo;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.xebialabs.restito.server.StubServer;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static ch.qos.logback.classic.Level.INFO;
import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.*;
import static com.xebialabs.restito.semantics.Condition.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.glassfish.grizzly.http.util.HttpStatus.OK_200;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@AutoConfigureObservability
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "management.tracing.enabled=true")
@ExtendWith(SpringExtension.class)
class ContextPropagationApiTest {
    @Value("${local.server.port}")
    private int portNumber;

    private StubServer server;
    private ListAppender<ILoggingEvent> listAppenderForController;
    private ListAppender<ILoggingEvent> listAppenderForService;

    @BeforeEach
    void setUp() {
        server = new StubServer(47923).run();
        whenHttp(server).match(
            get("/test")
        ).then(
            stringContent("success response from server"),
            status(OK_200)
        );

        listAppenderForController = startRecordingLogsFor(ContextPropagationController.class);
        listAppenderForService = startRecordingLogsFor(ContextPropagationService.class);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    // context propagation fails without Hooks.enableAutomaticContextPropagation()
    @Test
    void testBlock() {
        RestAssured.given()
            .port(portNumber)
            .when().get("/test-context-propagation-block")
            .then()
            .statusCode(OK.value());

        assertAllServiceLogsHaveTraceId(getTraceIdFromControllerLog());
    }

    // context propagation fails without Hooks.enableAutomaticContextPropagation()
    @Test
    void testSubscribeOnBoundedElastic() throws InterruptedException {
        RestAssured.given()
            .port(portNumber)
            .when()
            .get("/test-context-propagation-subscribe-without-webclient")
            .then()
            .statusCode(OK.value());

        // artificial delay due to non-blocking nature
        Thread.sleep(5000);

        assertAllServiceLogsHaveTraceId(getTraceIdFromControllerLog());
    }

    // context propagation fails without Hooks.enableAutomaticContextPropagation()
    @Test
    void testSubscribe() throws InterruptedException {
        RestAssured.given()
            .port(portNumber)
            .when()
            .get("/test-context-propagation-subscribe-with-reactive-web-client")
            .then()
            .statusCode(OK.value());

        // artificial delay due to non-blocking nature
        Thread.sleep(5000);

        assertAllServiceLogsHaveTraceId(getTraceIdFromControllerLog());
    }


    private String getTraceIdFromControllerLog() {
        final List<ILoggingEvent> controllerLogs = getLogsByLevel(listAppenderForController, INFO);
        assertThat(controllerLogs).isNotEmpty();

        final Map<String, String> mdcPropertyMap = controllerLogs.get(0).getMDCPropertyMap();
        assertThat(mdcPropertyMap).containsKey("traceId");
        return mdcPropertyMap.get("traceId");
    }

    private void assertAllServiceLogsHaveTraceId(String traceId) {
        final List<ILoggingEvent> allLogs = getLogsByLevel(listAppenderForService, INFO);
        final List<ILoggingEvent> logsWithoutTraceIdOrWithUnexpectedTraceId = allLogs.stream()
            .filter(logEvent -> !logEvent.getMDCPropertyMap().containsKey("traceId")
                || !logEvent.getMDCPropertyMap().get("traceId").equals(traceId))
            .toList();

        logsWithoutTraceIdOrWithUnexpectedTraceId.forEach(logEvent -> log.info("Expecting traceId={} in log={}",
            traceId, logEvent.getFormattedMessage()));
        assertThat(logsWithoutTraceIdOrWithUnexpectedTraceId).isEmpty();
    }

    private ListAppender<ILoggingEvent> startRecordingLogsFor(Class<?> clazz) {
        Logger logger = (Logger) getLogger(clazz);

        ListAppender<ILoggingEvent> loggingEventListAppender = new ListAppender<>();
        loggingEventListAppender.start();
        logger.addAppender(loggingEventListAppender);

        return loggingEventListAppender;
    }

    private List<ILoggingEvent> getLogsByLevel(ListAppender<ILoggingEvent> loggingEventListAppender,
                                               Level level) {
        return Collections
            .unmodifiableList(loggingEventListAppender.list)
            .stream().filter(loggingEvent -> Objects.equals(loggingEvent.getLevel(), level))
            .collect(Collectors.toList());
    }
}