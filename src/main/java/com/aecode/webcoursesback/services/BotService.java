package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.Bot.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface BotService {
    // AECOBOTS (INTERNAL)
    Page<BotCardDTO> listAecobotsPaged(String clerkId, Long categoryId, Pageable pageable);
    List<BotCardDTO> listAecobotsAll(String clerkId, Long categoryId); // para "ver todo"

    // AI TOOLS (EXTERNAL)
    Page<BotCardDTO> listExternalToolsPaged(String clerkId, Long categoryId, Pageable pageable);
    List<BotCardDTO> listExternalToolsAll(String clerkId, Long categoryId);

    // Favoritos (mis bots)
    Page<BotCardDTO> listMyBotsPaged(String clerkId, String type, Pageable pageable);
    void addFavorite(String clerkId, Long botId);
    void removeFavorite(String clerkId, Long botId);

    // CRUD admin
    BotCardDTO createBot(BotCreateUpdateDTO dto);
    BotCardDTO updateBot(Long botId, BotCreateUpdateDTO dto);
    void deleteBot(Long botId);

    // Utilidades
    BotLinkDTO getBotLink(Long botId);
}
