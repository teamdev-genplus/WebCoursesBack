package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.Bot.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface BotService {
    // AECObots (INTERNAL)
    Page<AecobotCardDTO> listAecobotsPaged(String clerkId, Long categoryId, Pageable pageable);
    List<AecobotCardDTO> listAecobotsAll(String clerkId, Long categoryId);

    // AI Tools (EXTERNAL)
    Page<ExternalToolCardDTO> listExternalToolsPaged(String clerkId, Long categoryId, Pageable pageable);
    List<ExternalToolCardDTO> listExternalToolsAll(String clerkId, Long categoryId);

    // Home (EXTERNAL): 1 destacado + 6
    ExternalToolsHomeDTO listExternalToolsHome(String clerkId, Long categoryId);

    // Favoritos
    Page<AecobotCardDTO> listMyInternalBotsPaged(String clerkId, Long categoryId, Pageable pageable);
    Page<ExternalToolCardDTO> listMyExternalBotsPaged(String clerkId, Long categoryId, Pageable pageable);


    // CRUD admin (devuelve DTO según type)
    AecobotCardDTO createOrUpdateInternal(BotCreateUpdateDTO dto, Long botIdOrNull);
    ExternalToolCardDTO createOrUpdateExternal(BotCreateUpdateDTO dto, Long botIdOrNull);

    void deleteBot(Long botId);

    // Favorito toggle
    void addFavorite(String clerkId, Long botId);
    void removeFavorite(String clerkId, Long botId);
}
