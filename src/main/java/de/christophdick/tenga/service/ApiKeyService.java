package de.christophdick.tenga.service;

public interface ApiKeyService {

    /**
     * Validate an API key
     */
    boolean validateApiKey(String apiKey);

    /**
     * Update last used timestamp for an API key
     */
    void updateLastUsed(String apiKey);

    /**
     * Create a new API key (for testing/setup)
     */
    String createApiKey();
}
