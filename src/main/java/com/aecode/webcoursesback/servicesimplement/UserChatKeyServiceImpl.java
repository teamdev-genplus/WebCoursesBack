package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.UserChatKeyDTO;
import com.aecode.webcoursesback.entities.UserChatKey;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.repositories.UserChatKeyRepository;
import com.aecode.webcoursesback.services.UserChatKeyService;
import jakarta.persistence.EntityNotFoundException;
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
    private final IUserProfileRepository userProfileRepository;

    @Override
    public void saveChatKey(UserChatKeyDTO dto) {
        // Validar que el usuario exista
        if (!userProfileRepository.existsByClerkId(dto.getClerkId())) {
            throw new EntityNotFoundException("Usuario con clerkId " + dto.getClerkId() + " no existe");
        }
        // Guardar clave encriptada
        String encrypted = encryptor.encrypt(dto.getChatKey());
        repository.save(UserChatKey.builder()
                .clerkId(dto.getClerkId())
                .chatKey(encrypted)
                .build());
    }

    @Override
    public UserChatKeyDTO getChatKeyByClerkId(String clerkId) {
        UserChatKey key = repository.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Clave de chat no encontrada"));
        String decrypted = encryptor.decrypt(key.getChatKey());
        return UserChatKeyDTO.builder()
                .clerkId(key.getClerkId())
                .chatKey(decrypted)
                .build();
    }

    @Override
    public UserChatKeyDTO updateChatKey(String clerkId, String newKey) {
        UserChatKey key = repository.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Clave de chat no encontrada"));
        key.setChatKey(encryptor.encrypt(newKey));
        repository.save(key);
        return UserChatKeyDTO.builder()
                .clerkId(clerkId)
                .chatKey(newKey)
                .build();
    }

    @Override
    public void deleteChatKey(String clerkId) {
        if (!repository.existsByClerkId(clerkId)) {
            throw new EntityNotFoundException("Clave de chat no encontrada");
        }
        repository.deleteByClerkId(clerkId);
    }
}
