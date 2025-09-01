package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Bot.*;
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

    // ======================= AECOBOTS (INTERNAL) =======================

    // Paginado (UI: muestra 6 por defecto)
    @GetMapping("/aecobots")
    public Page<BotCardDTO> listAecobotsPaged(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return botService.listAecobotsPaged(clerkId, categoryId, PageRequest.of(page, size, Sort.by("title").ascending()));
    }

    // "Ver todo"
    @GetMapping("/aecobots/all")
    public List<BotCardDTO> listAecobotsAll(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId
    ) {
        return botService.listAecobotsAll(clerkId, categoryId);
    }

    // ======================= AI TOOLS (EXTERNAL) =======================

    @GetMapping("/aitools")
    public Page<BotCardDTO> listExternalToolsPaged(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return botService.listExternalToolsPaged(clerkId, categoryId, PageRequest.of(page, size, Sort.by("title").ascending()));
    }

    @GetMapping("/aitools/all")
    public List<BotCardDTO> listExternalToolsAll(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId
    ) {
        return botService.listExternalToolsAll(clerkId, categoryId);
    }

    // ======================= FAVORITOS (MIS BOTS) =======================

    // Mis bots (favorites): type opcional = INTERNAL | EXTERNAL
    @GetMapping("/favorites")
    public Page<BotCardDTO> listMyBots(
            @RequestParam String clerkId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return botService.listMyBotsPaged(clerkId, type, PageRequest.of(page, size, Sort.by("title").ascending()));
    }

    // Toggle favorito (ADD)
    @PostMapping("/{botId}/favorite")
    public void addFavorite(@PathVariable Long botId, @RequestParam String clerkId) {
        botService.addFavorite(clerkId, botId);
    }

    // Toggle favorito (REMOVE)
    @DeleteMapping("/{botId}/favorite")
    public void removeFavorite(@PathVariable Long botId, @RequestParam String clerkId) {
        botService.removeFavorite(clerkId, botId);
    }

    // ======================= LINK =======================
    @GetMapping("/{botId}/link")
    public BotLinkDTO getBotLink(@PathVariable Long botId) {
        return botService.getBotLink(botId);
    }

    // ======================= CRUD ADMIN =======================
    @PostMapping
    public BotCardDTO create(@RequestBody BotCreateUpdateDTO dto) {
        return botService.createBot(dto);
    }

    @PutMapping("/{botId}")
    public BotCardDTO update(@PathVariable Long botId, @RequestBody BotCreateUpdateDTO dto) {
        return botService.updateBot(botId, dto);
    }

    @DeleteMapping("/{botId}")
    public void delete(@PathVariable Long botId) {
        botService.deleteBot(botId);
    }
}
