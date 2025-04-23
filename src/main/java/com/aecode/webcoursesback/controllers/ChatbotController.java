package com.aecode.webcoursesback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aecode.webcoursesback.services.ChatbotService;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ChatbotController {

    @Autowired
    private ChatbotService openaiService;

    @GetMapping("/ask-openai")
    public Mono<String> askOpenai(@RequestParam String prompt) {
        return openaiService.getResponse(prompt);
    }

}
