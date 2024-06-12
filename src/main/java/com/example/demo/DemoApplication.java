package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.prometheus.client.exemplars.tracer.common.SpanContextSupplier;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	TracerSpanContextSupplier spanContextSuppler(Tracer tracer) {
		return new TracerSpanContextSupplier(tracer);
	}
	
	static class TracerSpanContextSupplier implements SpanContextSupplier {

		private final Tracer tracer;

		TracerSpanContextSupplier(Tracer tracer) {
			this.tracer = tracer;
		}

		@Override
		public String getTraceId() {
			Span currentSpan = currentSpan();
			return (currentSpan != null) ? currentSpan.context().traceId() : null;
		}

		@Override
		public String getSpanId() {
			Span currentSpan = currentSpan();
			return (currentSpan != null) ? currentSpan.context().spanId() : null;
		}

		@Override
		public boolean isSampled() {
			Span currentSpan = currentSpan();
			if (currentSpan == null) {
				return false;
			}
			Boolean sampled = currentSpan.context().sampled();
			return sampled != null && sampled;
		}

		private Span currentSpan() {
			return this.tracer.currentSpan();
		}

	}

}
