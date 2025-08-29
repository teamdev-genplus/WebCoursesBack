package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Bot.AccessStatusDTO;
import com.aecode.webcoursesback.dtos.Bot.GrantAccessRequestDTO;
import com.aecode.webcoursesback.services.BotAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bot-access")
@RequiredArgsConstructor
public class BotAccessController {
    private final BotAccessService botAccessService;

    // Concede acceso a un AECObot (free/paid/admin)
    @PostMapping("/grant")
    public AccessStatusDTO grant(@RequestBody GrantAccessRequestDTO request) {
        return botAccessService.grantAccess(request);
    }

    // Revoca acceso
    @DeleteMapping("/revoke")
    public AccessStatusDTO revoke(@RequestParam String clerkId, @RequestParam Long botId) {
        return botAccessService.revokeAccess(clerkId, botId);
    }

    // Lista mis AECObots con acceso
    @GetMapping("/my")
    public List<AccessStatusDTO> listMy(@RequestParam String clerkId) {
        return botAccessService.listMyAecobots(clerkId);
    }
}
