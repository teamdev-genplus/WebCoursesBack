package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Chatbot.ChatCreateRequestDTO;
import com.aecode.webcoursesback.dtos.Chatbot.ChatRenameRequestDTO;
import com.aecode.webcoursesback.dtos.Chatbot.ChatResponseDTO;
import com.aecode.webcoursesback.dtos.Chatbot.MessageUsageResponseDTO;
import com.aecode.webcoursesback.entities.Chatbot.ChatSession;
import com.aecode.webcoursesback.entities.Chatbot.UserDailyChatUsage;
import com.aecode.webcoursesback.repositories.Chatbot.ChatSessionRepository;
import com.aecode.webcoursesback.repositories.Chatbot.UserDailyChatUsageRepository;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.ChatSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService{

    private static final int MAX_CHATS_PER_USER = 3;
    private static final int DAILY_MESSAGE_LIMIT = 8;

    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;

    private final ChatSessionRepository chatRepo;
    private final IUserProfileRepository userRepo;
    private final UserDailyChatUsageRepository usageRepo;

    // ---------- Chats ----------
    @Override
    @Transactional
    public ChatResponseDTO createChat(ChatCreateRequestDTO req) {
        // Validar usuario
        if (!userRepo.existsByClerkId(req.getClerkId())) {
            throw new EntityNotFoundException("Usuario no existe (clerkId inválido).");
        }
        // Validar límite de chats
        int current = chatRepo.countByClerkId(req.getClerkId());
        if (current >= MAX_CHATS_PER_USER) {
            throw new IllegalArgumentException("Has alcanzado el máximo de " + MAX_CHATS_PER_USER + " chats.");
        }

        String display = (req.getDisplayName() == null || req.getDisplayName().isBlank())
                ? "Chat " + (current + 1)
                : req.getDisplayName().trim();

        ChatSession session = ChatSession.builder()
                .clerkId(req.getClerkId())
                .displayName(display)
                .chatKeyEncrypted(encryptor.encrypt(req.getChatKey()))
                .active(true)
                .build();

        ChatSession saved = chatRepo.save(session);
        return ChatResponseDTO.builder()
                .chatId(saved.getId())
                .displayName(saved.getDisplayName())
                .chatKey(encryptor.decrypt(saved.getChatKeyEncrypted()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponseDTO> listChats(String clerkId) {
        if (!userRepo.existsByClerkId(clerkId)) {
            throw new EntityNotFoundException("Usuario no existe (clerkId inválido).");
        }
        return chatRepo.findByClerkIdOrderByCreatedAtDesc(clerkId).stream()
                .map(s -> ChatResponseDTO.builder()
                        .chatId(s.getId())
                        .displayName(s.getDisplayName())
                        .chatKey(encryptor.decrypt(s.getChatKeyEncrypted()))
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatResponseDTO renameChat(String clerkId, Long chatId, ChatRenameRequestDTO req) {
        ChatSession session = chatRepo.findByIdAndClerkId(chatId, clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Chat no encontrado o no pertenece al usuario."));
        if (req.getDisplayName() == null || req.getDisplayName().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        session.setDisplayName(req.getDisplayName().trim());
        ChatSession saved = chatRepo.save(session);

        return ChatResponseDTO.builder()
                .chatId(saved.getId())
                .displayName(saved.getDisplayName())
                .chatKey(encryptor.decrypt(saved.getChatKeyEncrypted()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteChat(String clerkId, Long chatId) {
        ChatSession session = chatRepo.findByIdAndClerkId(chatId, clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Chat no encontrado o no pertenece al usuario."));
        chatRepo.delete(session); // si prefieres "soft delete", marca active=false
    }

    // ---------- Uso diario ----------
    @Override
    @Transactional(readOnly = true)
    public MessageUsageResponseDTO getUsage(String clerkId) {
        if (!userRepo.existsByClerkId(clerkId)) {
            throw new EntityNotFoundException("Usuario no existe (clerkId inválido).");
        }
        LocalDate today = LocalDate.now();
        int count = usageRepo.findByClerkIdAndUsageDate(clerkId, today)
                .map(UserDailyChatUsage::getMessageCount)
                .orElse(0);
        int remaining = Math.max(0, DAILY_MESSAGE_LIMIT - count);

        return MessageUsageResponseDTO.builder()
                .allowed(remaining > 0)
                .remaining(remaining)
                .resetsAt(today.plusDays(1))
                .message(remaining > 0
                        ? ("Puedes enviar " + remaining + " mensaje(s) más hoy.")
                        : "Has alcanzado el límite diario. Inténtalo de nuevo mañana.")
                .build();
    }

    @Override
    @Transactional
    public MessageUsageResponseDTO incrementUsage(String clerkId) {
        if (!userRepo.existsByClerkId(clerkId)) {
            throw new EntityNotFoundException("Usuario no existe (clerkId inválido).");
        }
        LocalDate today = LocalDate.now();
        UserDailyChatUsage usage = usageRepo.findByClerkIdAndUsageDate(clerkId, today)
                .orElseGet(() -> UserDailyChatUsage.builder()
                        .clerkId(clerkId)
                        .usageDate(today)
                        .messageCount(0)
                        .build());

        if (usage.getMessageCount() >= DAILY_MESSAGE_LIMIT) {
            // Ya alcanzó el límite
            return MessageUsageResponseDTO.builder()
                    .allowed(false)
                    .remaining(0)
                    .resetsAt(today.plusDays(1))
                    .message("Has alcanzado el límite diario. Inténtalo de nuevo mañana.")
                    .build();
        }

        usage.setMessageCount(usage.getMessageCount() + 1);
        usageRepo.save(usage);

        int remaining = DAILY_MESSAGE_LIMIT - usage.getMessageCount();
        return MessageUsageResponseDTO.builder()
                .allowed(true)
                .remaining(Math.max(0, remaining))
                .resetsAt(today.plusDays(1))
                .message("Mensaje contado. Te quedan " + Math.max(0, remaining) + " para hoy.")
                .build();
    }
}
