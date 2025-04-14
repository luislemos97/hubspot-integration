package com.meetime.hubspotintegration.controller;

import com.meetime.hubspotintegration.service.HubSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HubSpotController {

    @Autowired
    private HubSpotService hubSpotService;

    @GetMapping("/auth/url")
    public ResponseEntity<String> getAuthorizationUrl() {
        String authUrl = hubSpotService.generateAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<?> handleCallback(@RequestParam("code") String code,
                                            @RequestParam(name = "state", required = false) String state) {
        try {
            String accessToken = hubSpotService.exchangeCodeForAccessToken(code);
            return ResponseEntity.ok("Access Token: " + accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao trocar o código: " + e.getMessage());
        }
    }

    @PostMapping("/contacts")
    public ResponseEntity<?> createContact(@RequestBody Map<String, Object> contactDetails) {
        try {
            String response = hubSpotService.createContact(contactDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar contato: " + e.getMessage());
        }
    }

    @PostMapping("/webhook/contacts")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Recebido webhook: " + payload);
        return ResponseEntity.ok("Webhook recebido com sucesso!");
    }
}
