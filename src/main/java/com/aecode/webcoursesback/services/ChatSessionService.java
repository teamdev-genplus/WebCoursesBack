package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Chatbot.ChatCreateRequestDTO;
import com.aecode.webcoursesback.dtos.Chatbot.ChatRenameRequestDTO;
import com.aecode.webcoursesback.dtos.Chatbot.ChatResponseDTO;
import com.aecode.webcoursesback.dtos.Chatbot.MessageUsageResponseDTO;

import java.util.List;

public interface ChatSessionService {
    ChatResponseDTO createChat(ChatCreateRequestDTO req);
    List<ChatResponseDTO> listChats(String clerkId);
    ChatResponseDTO renameChat(String clerkId, Long chatId, ChatRenameRequestDTO req);
    void deleteChat(String clerkId, Long chatId);

    MessageUsageResponseDTO getUsage(String clerkId);
    MessageUsageResponseDTO incrementUsage(String clerkId);
}
