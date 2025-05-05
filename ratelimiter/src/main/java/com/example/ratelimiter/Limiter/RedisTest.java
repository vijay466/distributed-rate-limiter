package com.example.ratelimiter.Limiter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void testConnection() {
        try {
            redisTemplate.opsForValue().set("test", "value");
            String result = redisTemplate.opsForValue().get("test");
            System.out.println("Redis Connection Test Passed: " + result);
        } catch (Exception e) {
            System.err.println("Redis Connection Failed: " + e.getMessage());
        }
    }
}
