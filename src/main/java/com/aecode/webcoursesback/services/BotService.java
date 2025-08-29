package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.Bot.AecobotCardDTO;
import com.aecode.webcoursesback.dtos.Bot.BotCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Bot.BotLinkDTO;
import com.aecode.webcoursesback.dtos.Bot.ExternalToolCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface BotService {
    // Listados para UI
    List<AecobotCardDTO> listAecobotsForHome(String clerkId);        // INTERNAL
    List<ExternalToolCardDTO> listExternalToolsForHome();            // EXTERNAL

    // CRUD admin
    AecobotCardDTO createBot(BotCreateUpdateDTO dto);                // retorna forma card si INTERNAL
    AecobotCardDTO updateBot(Long botId, BotCreateUpdateDTO dto);
    void deleteBot(Long botId);

    // Utilidades
    BotLinkDTO getBotLink(Long botId);                               // redirección segura
    Page<AecobotCardDTO> listAecobotsPaged(String clerkId, Pageable pageable);
    Page<ExternalToolCardDTO> listExternalToolsPaged(Pageable pageable);
}
