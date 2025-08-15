package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Chatbot.*;
import com.aecode.webcoursesback.services.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatSessionController {
    private final ChatSessionService service;

    // Crear chat (valida máximo 3)
    @PostMapping
    public ResponseEntity<ChatResponseDTO> create(@RequestBody ChatCreateRequestDTO req) {
        return ResponseEntity.ok(service.createChat(req));
    }

    // Listar chats del usuario (nombre y chatKey desencriptada)
    @GetMapping("/{clerkId}")
    public ResponseEntity<List<ChatResponseDTO>> list(@PathVariable String clerkId) {
        return ResponseEntity.ok(service.listChats(clerkId));
    }

    // Renombrar
    @PatchMapping("/{clerkId}/{chatId}/rename")
    public ResponseEntity<ChatResponseDTO> rename(
            @PathVariable String clerkId,
            @PathVariable Long chatId,
            @RequestBody ChatRenameRequestDTO req
    ) {
        return ResponseEntity.ok(service.renameChat(clerkId, chatId, req));
    }

    // Eliminar (propiedad/ownership por clerkId)
    @DeleteMapping("/{clerkId}/{chatId}")
    public ResponseEntity<Void> delete(
            @PathVariable String clerkId,
            @PathVariable Long chatId
    ) {
        service.deleteChat(clerkId, chatId);
        return ResponseEntity.noContent().build();
    }

    // Obtener uso de hoy (restantes y fecha de reset)
    @GetMapping("/usage/{clerkId}")
    public ResponseEntity<MessageUsageResponseDTO> getUsage(@PathVariable String clerkId) {
        return ResponseEntity.ok(service.getUsage(clerkId));
    }

    // Incrementar contador al enviar mensaje (bloquea si llegó al límite)
    @PostMapping("/usage/increment")
    public ResponseEntity<MessageUsageResponseDTO> increment(@RequestBody UsageIncrementRequestDTO req) {
        MessageUsageResponseDTO res = service.incrementUsage(req.getClerkId());
        // si no está permitido, sería ideal devolver 429, pero tu handler global no lo maneja;
        // devolvemos 200 con allowed=false y message amigable.
        return ResponseEntity.ok(res);
    }
}
