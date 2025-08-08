package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.UserChatKeyDTO;
import com.aecode.webcoursesback.services.UserChatKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-key")
@RequiredArgsConstructor
public class UserChatKeyController {
    private final UserChatKeyService service;

    @PostMapping
    public ResponseEntity<UserChatKeyDTO> save(@RequestBody UserChatKeyDTO dto) {
        return ResponseEntity.ok(service.saveChatKey(dto));
    }

    @GetMapping("/{clerkId}")
    public ResponseEntity<UserChatKeyDTO> get(@PathVariable String clerkId) {
        return ResponseEntity.ok(service.getChatKeyByClerkId(clerkId));
    }

    @PutMapping("/{clerkId}")
    public ResponseEntity<UserChatKeyDTO> update(@PathVariable String clerkId, @RequestBody String newKey) {
        return ResponseEntity.ok(service.updateChatKey(clerkId, newKey));
    }

    @DeleteMapping("/{clerkId}")
    public ResponseEntity<Void> delete(@PathVariable String clerkId) {
        service.deleteChatKey(clerkId);
        return ResponseEntity.noContent().build();
    }
}
