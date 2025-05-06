package com.example.ratelimiter.Limiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript<Long> tokenBucketLuaScript;

    private final int CAPACITY = 10;
    private final int REFILL_RATE = 1;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String redisKey = "rate_limit:bucket:" + ip;
        long now = Instant.now().getEpochSecond();

        Long allowed = redisTemplate.execute(
                tokenBucketLuaScript,
                Collections.singletonList(redisKey),
                String.valueOf(CAPACITY),
                String.valueOf(REFILL_RATE),
                String.valueOf(now)
        );

        if (allowed == null || allowed == 0) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
