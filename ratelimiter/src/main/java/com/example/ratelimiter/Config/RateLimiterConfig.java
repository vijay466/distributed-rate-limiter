package com.example.ratelimiter.Config;

import com.example.ratelimiter.Limiter.RateLimiterFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RateLimiterConfig {

    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimiterFilter() {
        FilterRegistrationBean<RateLimiterFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimiterFilter());
        registrationBean.addUrlPatterns("/api/*"); // Apply to all abuse endpoints
        return registrationBean;
    }

    @Bean
    public DefaultRedisScript<Long> tokenBucketLuaScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText("""
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refill_rate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])

            local data = redis.call("HMGET", key, "tokens", "last_refill")
            local tokens = tonumber(data[1])
            local last_refill = tonumber(data[2])

            if tokens == nil then
                tokens = capacity
                last_refill = now
            end

            local delta = math.max(0, now - last_refill)
            local refill = delta * refill_rate
            tokens = math.min(capacity, tokens + refill)

            if tokens < 1 then
                return 0
            else
                tokens = tokens - 1
                redis.call("HMSET", key, "tokens", tokens, "last_refill", now)
                redis.call("EXPIRE", key, 3600)
                return 1
            end
        """);
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
