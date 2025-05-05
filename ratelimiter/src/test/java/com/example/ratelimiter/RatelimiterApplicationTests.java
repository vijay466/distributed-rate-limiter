package com.example.ratelimiter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@TestConfiguration
class TestConfig {
	@Bean
	public StringRedisTemplate stringRedisTemplate() {
		return Mockito.mock(StringRedisTemplate.class);
	}
}
class RatelimiterApplicationTests {

	@Test
	void contextLoads() {
		// No-op test to load context
	}
}
