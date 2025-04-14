package com.meetime.hubspotintegration.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class HubSpotService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${hubspot.client_id}")
    private String clientId;

    @Value("${hubspot.client_secret}")
    private String clientSecret;

    @Value("${hubspot.redirect_uri}")
    private String redirectUri;

    @Value("${hubspot.scopes:contacts}")
    private String scopes;

    @Value("${hubspot.access_token:}")
    private String accessToken;

    public String generateAuthorizationUrl() {
        String baseUrl = "https://app.hubspot.com/oauth/authorize";
        String url = baseUrl +
                "?client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scopes, StandardCharsets.UTF_8) +
                "&response_type=code";
        return url;
    }
    
    public String exchangeCodeForAccessToken(String code) {
        String tokenUrl = "https://api.hubapi.com/oauth/v1/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        } else {
            throw new RuntimeException("Erro ao obter access token do HubSpot");
        }
    }

    public String createContact(Map<String, Object> contactDetails) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Access Token não definido. Realize o fluxo OAuth para obtê-lo.");
        }

        String url = "https://api.hubapi.com/crm/v3/objects/contacts";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(contactDetails, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Erro ao criar contato. Status: " + response.getStatusCode());
        }
    }
}
