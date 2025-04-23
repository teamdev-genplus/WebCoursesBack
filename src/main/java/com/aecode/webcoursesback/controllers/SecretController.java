package com.aecode.webcoursesback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import com.aecode.webcoursesback.services.SecretManagerService;

public class SecretController {
    @Autowired
    private SecretManagerService secretManagerService;

    @GetMapping("/get-assistant-key")
    public String getAssistantKeyCredentials() {
        String secretValue = secretManagerService.getSecret("assistant-key");

        return secretValue;
    }

    @GetMapping("/get-open-ai-key")
    public String getOpenaiKeyCredentials() {
        String secretValue = secretManagerService.getSecret("open-ai-key");

        return secretValue;
    }

}
