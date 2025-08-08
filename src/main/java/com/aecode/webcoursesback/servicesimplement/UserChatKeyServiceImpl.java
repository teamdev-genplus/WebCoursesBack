package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.UserChatKeyDTO;
import com.aecode.webcoursesback.entities.UserChatKey;
import com.aecode.webcoursesback.repositories.UserChatKeyRepository;
import com.aecode.webcoursesback.services.UserChatKeyService;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserChatKeyServiceImpl implements UserChatKeyService {
    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;
    private final UserChatKeyRepository repository;

    @Override
    public UserChatKeyDTO saveChatKey(UserChatKeyDTO dto) {
        String encrypted = encryptor.encrypt(dto.getChatKey());
        UserChatKey saved = repository.save(UserChatKey.builder()
                .clerkId(dto.getClerkId())
                .chatKey(encrypted)
                .build());
        return UserChatKeyDTO.builder()
                .clerkId(saved.getClerkId())
                .chatKey(dto.getChatKey())
                .build();
    }

    @Override
    public UserChatKeyDTO getChatKeyByClerkId(String clerkId) {
        UserChatKey key = repository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("Clave no encontrada"));
        String decrypted = encryptor.decrypt(key.getChatKey());
        return UserChatKeyDTO.builder()
                .clerkId(key.getClerkId())
                .chatKey(decrypted)
                .build();
    }

    @Override
    public UserChatKeyDTO updateChatKey(String clerkId, String newKey) {
        UserChatKey key = repository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("Clave no encontrada"));
        key.setChatKey(encryptor.encrypt(newKey));
        UserChatKey updated = repository.save(key);
        return UserChatKeyDTO.builder()
                .clerkId(updated.getClerkId())
                .chatKey(newKey)
                .build();
    }

    @Override
    public void deleteChatKey(String clerkId) {
        repository.deleteByClerkId(clerkId);
    }
}
