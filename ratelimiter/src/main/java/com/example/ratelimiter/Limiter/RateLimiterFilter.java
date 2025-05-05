package com.example.ratelimiter.Limiter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final int MAX_REQUESTS = 5;  // Max requests allowed per minute
    private final long TIME_WINDOW = 60;  // Time window for rate limit (in seconds)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String redisKey = "rate_limit:" + ip;

        // Get current request count from Redis
        String currentCount = redisTemplate.opsForValue().get(redisKey);

        if (currentCount == null) {
            // No requests yet for this IP, initialize with 1
            redisTemplate.opsForValue().set(redisKey, "1", TIME_WINDOW, TimeUnit.SECONDS);
        } else {
            int currentRequests = Integer.parseInt(currentCount);
            if (currentRequests >= MAX_REQUESTS) {
                // Too many requests, reject with HTTP 429 (Too Many Requests)
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return;
            }
            // Increment the request count
            redisTemplate.opsForValue().increment(redisKey);
        }

        filterChain.doFilter(request, response);
    }
}
