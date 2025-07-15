// In AsyncConfig.java
package com.flowna.app.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer; // Import this
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor; // Import this

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer { // Implement AsyncConfigurer

    @Bean(name = "asyncExecutorCore")
    public ThreadPoolTaskExecutor asyncExecutorCore() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncSecCore-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        // This wraps the core executor to propagate security context
        return new DelegatingSecurityContextAsyncTaskExecutor(asyncExecutorCore());
    }

    // This method tells Spring's @Async which executor to use by default
    @Override
    public Executor getAsyncExecutor() {
        // Return the bean that wraps the executor with security context propagation
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // Optional: configure a default exception handler for @Async methods
        // You can return null or provide a custom handler
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
        // Or: return new SimpleAsyncUncaughtExceptionHandler();
        // Or: return new CustomAsyncExceptionHandler();
    }
}