package com.flowna.app.Logic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component // Replace with your actual component type
public class ThreadChecker {

        // Get an SLF4J Logger instance for this class
        private static final Logger log = LoggerFactory.getLogger(ThreadChecker.class);

        // Example method where you want to check the security context
        public void checkCurrentAuthentication(String executionPoint) {

            // 1. Get the Security Context for the current thread
            SecurityContext securityContext = SecurityContextHolder.getContext();

            // 2. Get the Authentication object from the context
            Authentication authentication = securityContext.getAuthentication();

            // 3. Get the current thread name for context
            String threadName = Thread.currentThread().getName();

            // 4. Log the details
            if (authentication == null) {
                log.info("[{}] - No Authentication found in SecurityContext on thread '{}'",
                        executionPoint, threadName);
            } else {
                log.info("[{}] - Authentication found on thread '{}': Principal='{}', Authenticated='{}', Authorities='{}'",
                        executionPoint,
                        threadName,
                        authentication.getName(), // Gets the username usually
                        authentication.isAuthenticated(),
                        authentication.getAuthorities() // Gets the roles/permissions
                );
                // You could log authentication.getPrincipal() too, but it might be a complex object
                // log.info("Principal object type: {}", authentication.getPrincipal().getClass().getName());
            }
        }

}
