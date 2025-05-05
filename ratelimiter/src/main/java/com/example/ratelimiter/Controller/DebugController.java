package com.example.ratelimiter.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/ping-redis")
    public String pingRedis() {
        try {
            redisTemplate.opsForValue().set("hello", "world");
            return "Redis works!";
        } catch (Exception e) {
            return "Redis error: " + e.getMessage();
        }
    }
}
