package com.example.ratelimiter.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AbuseSimulatorController {

    // CPU Abuse endpoint
    @GetMapping("/api/cpu")
    public String simulateCpuAbuse() {
        try {
            // Simulate CPU stress by running a heavy loop
            long sum = 0;
            for (long i = 0; i < 1000000000L; i++) {
                sum += i;
            }
            return "CPU stress test completed, sum: " + sum;
        } catch (Exception e) {
            return "Error during CPU stress: " + e.getMessage();
        }
    }

    // Memory Abuse endpoint
    @GetMapping("/api/memory")
    public String eatMemory() {
        List<byte[]> memory = new ArrayList<>();
        while (true) {
            memory.add(new byte[1024 * 1024]); // Allocate 1MB repeatedly
        }
    }

    // I/O Abuse endpoint
    @GetMapping("/api/io")
    public String simulateSlowIo() throws InterruptedException {
        Thread.sleep(10000); // Simulate blocking I/O by sleeping for 10 seconds
        return "Slow I/O Simulated";
    }
}
