package com.flowna.app.studySquad.ExamScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;




@Configuration
public class SchedulerConfig {

    @Bean(name = "webSocketScheduler")
    public ThreadPoolTaskScheduler webSocketScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // You can tune this pool specifically for its workload.
        // Since sending messages is very fast, you may not need a large pool.
        scheduler.setPoolSize(3);
        scheduler.setThreadNamePrefix("WsScheduler-");
        scheduler.initialize();
        return scheduler;
    }
}