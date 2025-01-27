package com.stockbetting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for StockbettingApplication
 * Verifies Spring context loading and application configuration
 */
@SpringBootTest
@DisplayName("StockbettingApplication Integration Tests")
class StockbettingApplicationTests {

	@Test
	@DisplayName("Spring context loads successfully")
	void whenApplicationStarts_thenContextLoads() {
		// Context load verification is handled by Spring Test framework
	}
}
