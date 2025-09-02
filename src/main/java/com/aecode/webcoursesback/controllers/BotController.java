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
    @GetMapping("/aecobots")
    public Page<AecobotCardDTO> listAecobotsPaged(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return botService.listAecobotsPaged(clerkId, categoryId, PageRequest.of(page, size));
    }

    @GetMapping("/aecobots/all")
    public List<AecobotCardDTO> listAecobotsAll(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId
    ) {
        return botService.listAecobotsAll(clerkId, categoryId);
    }

    // ======================= AI TOOLS (EXTERNAL) =======================
    @GetMapping("/aitools")
    public Page<ExternalToolCardDTO> listExternalToolsPaged(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size // si quieres 7 por página en la vista general
    ) {
        return botService.listExternalToolsPaged(clerkId, categoryId, PageRequest.of(page, size));
    }

    // Vista home: 1 destacado + 6
    @GetMapping("/aitools/home")
    public ExternalToolsHomeDTO listExternalToolsHome(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId
    ) {
        return botService.listExternalToolsHome(clerkId, categoryId);
    }

    @GetMapping("/aitools/all")
    public List<ExternalToolCardDTO> listExternalToolsAll(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) Long categoryId
    ) {
        return botService.listExternalToolsAll(clerkId, categoryId);
    }

    // ======================= FAVORITOS =======================
    // INTERNAL favorites con filtro opcional por categoría
    @GetMapping("/favorites/internal")
    public Page<AecobotCardDTO> listMyInternalBots(
            @RequestParam String clerkId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return botService.listMyInternalBotsPaged(clerkId, categoryId, PageRequest.of(page, size));
    }

    // EXTERNAL favorites con filtro opcional por categoría
    @GetMapping("/favorites/external")
    public Page<ExternalToolCardDTO> listMyExternalBots(
            @RequestParam String clerkId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return botService.listMyExternalBotsPaged(clerkId, categoryId, PageRequest.of(page, size));
    }

    // Toggle favorito
    @PostMapping("/{botId}/favorite")
    public void addFavorite(@PathVariable Long botId, @RequestParam String clerkId) {
        botService.addFavorite(clerkId, botId);
    }

    @DeleteMapping("/{botId}/favorite")
    public void removeFavorite(@PathVariable Long botId, @RequestParam String clerkId) {
        botService.removeFavorite(clerkId, botId);
    }

    // ======================= CRUD ADMIN =======================
    // Crear/actualizar INTERNAL
    @PostMapping("/internal")
    public AecobotCardDTO createInternal(@RequestBody BotCreateUpdateDTO dto) {
        return botService.createOrUpdateInternal(dto, null);
    }

    @PutMapping("/internal/{botId}")
    public AecobotCardDTO updateInternal(@PathVariable Long botId, @RequestBody BotCreateUpdateDTO dto) {
        return botService.createOrUpdateInternal(dto, botId);
    }

    // Crear/actualizar EXTERNAL
    @PostMapping("/external")
    public ExternalToolCardDTO createExternal(@RequestBody BotCreateUpdateDTO dto) {
        return botService.createOrUpdateExternal(dto, null);
    }

    @PutMapping("/external/{botId}")
    public ExternalToolCardDTO updateExternal(@PathVariable Long botId, @RequestBody BotCreateUpdateDTO dto) {
        return botService.createOrUpdateExternal(dto, botId);
    }

    @DeleteMapping("/{botId}")
    public void delete(@PathVariable Long botId) {
        botService.deleteBot(botId);
    }
}
