# Distributed Rate Limiter using Spring Boot and Redis

## Overview

This project demonstrates a **distributed rate limiter** using the **Token Bucket algorithm**, implemented in **Spring Boot** with **Redis** as the central store. The application simulates abuse scenarios like CPU, Memory, and IO-intensive endpoints.

## Features

* Token bucket-based rate limiting per client IP
* Redis-backed token bucket state storage
* Simulates abuse endpoints: `/api/cpu`, `/api/memory`, `/api/io`
* Configurable rate limits
* Centralized rate limiting across multiple instances

## Technologies Used

* Java 17
* Spring Boot 3+
* Redis
* Lua (for atomic Redis operations)

---

## Architecture

```
             [Client/User]
                  |
           [Spring Boot Instance]
                  |
         [Rate Limiting Filter]
                  |
        [Token Bucket Logic in Redis]
                  |
              [Redis Server]
```

Each Spring Boot instance shares rate limit state via Redis, so all requests from a single IP are rate-limited globally.

---

## Rate Limiting Details

* **Algorithm**: Token Bucket
* **Refill Rate**: 1 token/second (configurable)
* **Capacity**: 5 tokens per IP (configurable)
* When tokens are exhausted, the API returns HTTP `429 Too Many Requests`

---

## Endpoints

| Endpoint      | Description            |
| ------------- | ---------------------- |
| `/api/cpu`    | Simulates CPU abuse    |
| `/api/memory` | Simulates memory abuse |
| `/api/io`     | Simulates I/O abuse    |

---

## Project Structure

```
ratelimiter/
├── src/
│   ├── main/
│   │   ├── java/com/example/ratelimiter/
│   │   │   ├── controller/       # Abuse simulation endpoints
│   │   │   ├── limiter/          # Token bucket filter + Redis Lua logic
│   │   │   ├── config/           # Redis config and filter registration
│   │   │   └── RateLimiterApp.java
│   │   └── resources/
│   │       └── application.properties
```

---

## application.properties

```
spring.application.name=ratelimiter
spring.main.allow-bean-definition-overriding=true

# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.timeout=2000
spring.redis.jedis.pool.max-active=10
spring.redis.jedis.pool.max-idle=5
spring.redis.jedis.pool.min-idle=1
spring.redis.jedis.pool.max-wait=2000
```

---

## Running the Project

### Prerequisites

* JDK 17+
* Maven
* Redis running locally on port 6379

### Steps

1. **Start Redis locally** (if not already running):

   ```bash
   redis-server
   ```

2. **Clone and build the project:**

   ```bash
   git clone <this-repo-url>
   cd ratelimiter
   mvn clean install
   ```

3. **Run multiple Spring Boot instances (in separate terminals):**

   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8083
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8084
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085
   ```

---

## Testing Rate Limiting

### Basic cURL Test

```bash
for i in {1..10}; do
  curl -i http://localhost:8081/api/cpu
done
```

You should see HTTP 429 after 5 requests from the same IP.

### Stress Test

```bash
#!/bin/bash
for i in {1..1000}; do
  curl -s http://localhost:8081/api/cpu > /dev/null &
done
wait
```

---

## Token Bucket Logic (Lua script inside Java)

The token bucket is implemented using Lua script executed on Redis, ensuring atomic operations when fetching tokens, refilling, and updating timestamps.

```lua
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
else
  local elapsed = now - last_refill
  local refill = math.floor(elapsed * refill_rate)
  tokens = math.min(capacity, tokens + refill)
  last_refill = now
end

if tokens > 0 then
  tokens = tokens - 1
  redis.call("HMSET", key, "tokens", tokens, "last_refill", last_refill)
  redis.call("EXPIRE", key, 60)
  return 1
else
  return 0
end
```

---
