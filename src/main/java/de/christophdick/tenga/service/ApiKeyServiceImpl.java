package de.christophdick.tenga.service;

import de.christophdick.tenga.entity.ApiKey;
import de.christophdick.tenga.repository.ApiKeyRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return false;
        }

        // For MVP: simple implementation
        // In production, we'd hash the key and compare hashes
        return apiKeyRepository.findAll().stream()
            .anyMatch(stored -> passwordEncoder.matches(apiKey, stored.getKeyHash()));
    }

    @Override
    public void updateLastUsed(String apiKey) {
        apiKeyRepository.findAll().stream()
            .filter(stored -> passwordEncoder.matches(apiKey, stored.getKeyHash()))
            .findFirst()
            .ifPresent(key -> {
                key.updateLastUsedAt();
                apiKeyRepository.save(key);
            });
    }

    @Override
    public String createApiKey() {
        String rawKey = UUID.randomUUID().toString();
        String hashedKey = passwordEncoder.encode(rawKey);

        ApiKey apiKey = new ApiKey(hashedKey);
        apiKeyRepository.save(apiKey);

        return rawKey;
    }
}
