package com.flowna.app.config;

            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import org.springframework.messaging.simp.config.MessageBrokerRegistry;
            import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
            import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
            import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
            import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

            @Configuration
            @EnableWebSocketMessageBroker
            public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

                @Override
                public void configureMessageBroker(MessageBrokerRegistry config) {
                    config.enableSimpleBroker("/topic", "/queue")
                            .setTaskScheduler(heartbeatTaskScheduler())
                            .setHeartbeatValue(new long[]{10000, 10000});
                    config.setApplicationDestinationPrefixes("/app");
                }

                @Override
                public void registerStompEndpoints(StompEndpointRegistry registry) {
                    registry.addEndpoint("/ws")
                            .setAllowedOriginPatterns("http://localhost:3000/","https://app.flowna.io", "https://app.flowna.io/")
                            .withSockJS();
                }

                @Bean
                public ThreadPoolTaskScheduler heartbeatTaskScheduler() {
                    return new ThreadPoolTaskScheduler();
                }
            }