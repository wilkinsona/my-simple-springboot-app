package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;

// Application hangs after upgrading to spring boot 3.2.x
@SpringBootTest
@AutoConfigureObservability
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
