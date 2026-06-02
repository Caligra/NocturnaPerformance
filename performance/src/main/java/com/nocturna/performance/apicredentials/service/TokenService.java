package com.nocturna.performance.apicredentials.service;

import com.nocturna.performance.apicredentials.dto.*;
import com.nocturna.performance.apicredentials.repository.APICredentialsRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class TokenService {
    private final APICredentialsRepository apiRepo;
    private final RestTemplate restTemplate;
    public TokenService(APICredentialsRepository apiRepo, RestTemplate restTemplate) {
        this.apiRepo = apiRepo;
        this.restTemplate = restTemplate;
    }

    public String getValidToken() {
        APICredentials creds = apiRepo.findById(1).orElseThrow();
        if (creds.getTokenValue() == null || Instant.now().isAfter(creds.getExpireTime())) {
            refreshToken(creds);
        }
        return creds.getTokenValue();
    }

    public void refreshToken(APICredentials credentials) {
        // Build request body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", credentials.getClientId());
        body.add("client_secret", credentials.getClientSecret());

        // Build headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // POST call
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "https://nocturna-dev-store.myshopify.com/admin/oauth/access_token",
                request,
                TokenResponse.class
        );
        TokenResponse tokenResponse = response.getBody();
        // Update DB row with new token + expiry
        credentials.setTokenValue(tokenResponse.getAccessToken());
        credentials.setExpireTime(Instant.now().plusSeconds(tokenResponse.getExpiresIn()));
        apiRepo.save(credentials);
    }
}