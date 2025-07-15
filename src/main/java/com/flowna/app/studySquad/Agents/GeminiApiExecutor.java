package com.flowna.app.studySquad.Agents;

import org.springframework.http.HttpEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class GeminiApiExecutor {

    /**
     * Executes a POST request using RestTemplate and retries on failure.
     * It will attempt the call up to 5 times with an exponential backoff
     * starting at 5 seconds, only for exceptions related to REST calls.
     *
     * @param restTemplate  The RestTemplate instance to use.
     * @param url           The URL for the API endpoint.
     * @param requestEntity The HTTP request entity.
     * @param responseType  The class of the expected response type.
     * @param contextLog    A string for logging the context of the call.
     * @param <T>           The type of the response body.
     * @return The response from the API call.
     * @throws RestClientException if the call fails after all retry attempts.
     */
    @Retryable(
            value = {RestClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0)
    )
    public <T> T postForObject(RestTemplate restTemplate, String url, HttpEntity<?> requestEntity, Class<T> responseType, String contextLog) {
        System.out.println("Attempting API call for: " + contextLog);
        return restTemplate.postForObject(url, requestEntity, responseType);
    }
}