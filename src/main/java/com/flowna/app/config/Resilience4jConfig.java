package com.flowna.app.config;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class Resilience4jConfig {

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(attempt -> {
                    // Add jitter: base wait duration + random value (0-500ms)
                    long jitter = ThreadLocalRandom.current().nextLong(500);
                    return 1000 + jitter;
                })
                .retryExceptions(IOException.class, java.net.http.HttpTimeoutException.class, com.flowna.app.Agents.Gemini.GeminiRateLimitException.class)
                .build();

        return RetryRegistry.of(config);
    }
}