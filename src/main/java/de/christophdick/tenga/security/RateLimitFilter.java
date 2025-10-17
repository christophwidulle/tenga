package de.christophdick.tenga.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.christophdick.tenga.mcp.dto.JsonRpcError;
import de.christophdick.tenga.mcp.dto.JsonRpcResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "Authorization";
    private static final String API_KEY_PREFIX = "Bearer ";

    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // Skip rate limiting for public endpoints
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract API key from request
        String apiKey = extractApiKey(request);

        if (apiKey != null) {
            // Check rate limit
            if (!rateLimiter.isAllowed(apiKey)) {
                handleRateLimitExceeded(request, response, apiKey);
                return;
            }

            // Add rate limit headers
            int remaining = rateLimiter.getRemainingRequests(apiKey);
            Instant resetTime = rateLimiter.getResetTime(apiKey);

            response.setHeader("X-RateLimit-Limit", "60");
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
            response.setHeader("X-RateLimit-Reset", formatResetTime(resetTime));
        }

        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        String authHeader = request.getHeader(API_KEY_HEADER);
        if (authHeader != null && authHeader.startsWith(API_KEY_PREFIX)) {
            return authHeader.substring(API_KEY_PREFIX.length());
        }
        return null;
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info") ||
               path.equals("/mcp/manifest");
    }

    private void handleRateLimitExceeded(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String apiKey) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");

        int remaining = rateLimiter.getRemainingRequests(apiKey);
        Instant resetTime = rateLimiter.getResetTime(apiKey);

        response.setHeader("X-RateLimit-Limit", "60");
        response.setHeader("X-RateLimit-Remaining", "0");
        response.setHeader("X-RateLimit-Reset", formatResetTime(resetTime));
        response.setHeader("Retry-After", String.valueOf(calculateRetryAfterSeconds(resetTime)));

        // Check if this is an MCP request
        if (request.getRequestURI().startsWith("/mcp")) {
            // Return JSON-RPC error for MCP requests
            JsonRpcError error = new JsonRpcError(
                JsonRpcError.RATE_LIMIT_ERROR,
                "Rate limit exceeded. Maximum 60 requests per minute.",
                String.format("Reset at: %s", formatResetTime(resetTime))
            );
            JsonRpcResponse jsonRpcResponse = new JsonRpcResponse(error, null);
            response.getWriter().write(objectMapper.writeValueAsString(jsonRpcResponse));
        } else {
            // Return standard error for REST API requests
            String errorJson = String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Maximum 60 requests per minute. Reset at: %s\"}",
                formatResetTime(resetTime)
            );
            response.getWriter().write(errorJson);
        }
    }

    private String formatResetTime(Instant resetTime) {
        return DateTimeFormatter.ISO_INSTANT
            .withZone(ZoneId.of("UTC"))
            .format(resetTime);
    }

    private long calculateRetryAfterSeconds(Instant resetTime) {
        return Math.max(1, resetTime.getEpochSecond() - Instant.now().getEpochSecond());
    }
}
