package com.flowna.app.Agents.Gemini;

public class GeminiRateLimitException extends RuntimeException {
    public GeminiRateLimitException(String message) {
        super(message);
    }
}