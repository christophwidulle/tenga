package de.christophdick.tenga.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);

    private final Map<String, RequestWindow> requestWindows = new ConcurrentHashMap<>();

    /**
     * Check if a request from the given API key is allowed.
     * Returns true if the request is within rate limits, false otherwise.
     */
    public boolean isAllowed(String apiKey) {
        RequestWindow window = requestWindows.computeIfAbsent(apiKey, k -> new RequestWindow());
        return window.tryConsume();
    }

    /**
     * Get remaining requests for the given API key.
     */
    public int getRemainingRequests(String apiKey) {
        RequestWindow window = requestWindows.get(apiKey);
        if (window == null) {
            return MAX_REQUESTS_PER_MINUTE;
        }
        return Math.max(0, MAX_REQUESTS_PER_MINUTE - window.getRequestCount());
    }

    /**
     * Get reset time for the given API key.
     */
    public Instant getResetTime(String apiKey) {
        RequestWindow window = requestWindows.get(apiKey);
        if (window == null) {
            return Instant.now().plus(WINDOW_DURATION);
        }
        return window.getResetTime();
    }

    /**
     * Clean up expired windows (should be called periodically).
     */
    public void cleanup() {
        Instant now = Instant.now();
        requestWindows.entrySet().removeIf(entry ->
            entry.getValue().getResetTime().isBefore(now)
        );
    }

    private static class RequestWindow {
        private Instant windowStart;
        private int requestCount;

        public RequestWindow() {
            this.windowStart = Instant.now();
            this.requestCount = 0;
        }

        public synchronized boolean tryConsume() {
            Instant now = Instant.now();

            // Reset window if expired
            if (now.isAfter(windowStart.plus(WINDOW_DURATION))) {
                windowStart = now;
                requestCount = 0;
            }

            // Check if within limit
            if (requestCount < MAX_REQUESTS_PER_MINUTE) {
                requestCount++;
                return true;
            }

            return false;
        }

        public synchronized int getRequestCount() {
            Instant now = Instant.now();
            if (now.isAfter(windowStart.plus(WINDOW_DURATION))) {
                return 0;
            }
            return requestCount;
        }

        public synchronized Instant getResetTime() {
            return windowStart.plus(WINDOW_DURATION);
        }
    }
}
