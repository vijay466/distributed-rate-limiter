This project implements a distributed rate limiter using Spring Boot and Redis. Here's a summary of what has been implemented:

IP-Based Rate Limiting:

A custom RateLimiterFilter limits requests from individual IPs to a defined maximum within a time window (e.g., 5 requests per minute). This is enforced on all /api/* endpoints.

Redis Integration:

Redis is used to store request counts for each IP, enabling distributed rate limiting across multiple Spring Boot instances, ensuring consistent rate-limiting across the system.

DDoS Simulation Endpoints:

Includes endpoints (/api/cpu, /api/memory, /api/io) to simulate resource abuse, allowing testing of the rate limiter under heavy load.

Horizontal Scalability:

The application can scale horizontally with multiple Spring Boot instances, all using Redis for shared rate-limiting data, managed via a load balancer.

Docker Setup:

Docker and Docker Compose are used for easy deployment and scaling, managing both the Spring Boot application and Redis containers.
