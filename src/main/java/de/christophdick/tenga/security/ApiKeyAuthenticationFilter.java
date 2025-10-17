package de.christophdick.tenga.security;

import de.christophdick.tenga.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "Authorization";
    private static final String API_KEY_PREFIX = "Bearer ";

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(API_KEY_HEADER);

        if (authHeader != null && authHeader.startsWith(API_KEY_PREFIX)) {
            String apiKey = authHeader.substring(API_KEY_PREFIX.length());

            if (apiKeyService.validateApiKey(apiKey)) {
                // Set authentication in security context
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("api-user", null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Update last used timestamp
                apiKeyService.updateLastUsed(apiKey);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid API key\"}");
                response.setContentType("application/json");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
