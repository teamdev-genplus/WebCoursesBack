package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Bot.AccessStatusDTO;
import com.aecode.webcoursesback.dtos.Bot.GrantAccessRequestDTO;

import java.util.List;

public interface BotAccessService {
    AccessStatusDTO grantAccess(GrantAccessRequestDTO request);
    AccessStatusDTO revokeAccess(String clerkId, Long botId);
    List<AccessStatusDTO> listMyAecobots(String clerkId);
}
