package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Bot.AecobotCardDTO;
import com.aecode.webcoursesback.dtos.Bot.BotCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Bot.BotLinkDTO;
import com.aecode.webcoursesback.dtos.Bot.ExternalToolCardDTO;
import com.aecode.webcoursesback.services.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bots")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;

    // --- Listas para UI ---

    // AECObots (INTERNAL) — incluye hasAccess usando clerkId
    @GetMapping("/aecobots")
    public List<AecobotCardDTO> listAecobotsForHome(@RequestParam(required = false) String clerkId) {
        return botService.listAecobotsForHome(clerkId);
    }

    // AI Tools (EXTERNAL) — solo cards
    @GetMapping("/aitools")
    public List<ExternalToolCardDTO> listExternalToolsForHome() {
        return botService.listExternalToolsForHome();
    }

    // Paginados (si los necesitas en alguna vista)
    @GetMapping("/aecobots/paged")
    public Page<AecobotCardDTO> listAecobotsPaged(
            @RequestParam(required = false) String clerkId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return botService.listAecobotsPaged(clerkId, PageRequest.of(page, size, Sort.by("title").ascending()));
    }

    @GetMapping("/aitools/paged")
    public Page<ExternalToolCardDTO> listExternalToolsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return botService.listExternalToolsPaged(PageRequest.of(page, size, Sort.by("title").ascending()));
    }

    // Link seguro para redirigir (no expones la URL en el card)
    @GetMapping("/{botId}/link")
    public BotLinkDTO getBotLink(@PathVariable Long botId) {
        return botService.getBotLink(botId);
    }

    // --- CRUD Admin ---

    @PostMapping
    public AecobotCardDTO create(@RequestBody BotCreateUpdateDTO dto) {
        return botService.createBot(dto);
    }

    @PutMapping("/{botId}")
    public AecobotCardDTO update(@PathVariable Long botId, @RequestBody BotCreateUpdateDTO dto) {
        return botService.updateBot(botId, dto);
    }

    @DeleteMapping("/{botId}")
    public void delete(@PathVariable Long botId) {
        botService.deleteBot(botId);
    }
}
