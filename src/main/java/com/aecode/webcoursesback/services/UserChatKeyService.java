package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.UserChatKeyDTO;

public interface UserChatKeyService {
    void saveChatKey(UserChatKeyDTO dto);
    UserChatKeyDTO getChatKeyByClerkId(String clerkId);
    UserChatKeyDTO updateChatKey(String clerkId, String newKey);
    void deleteChatKey(String clerkId);
}
