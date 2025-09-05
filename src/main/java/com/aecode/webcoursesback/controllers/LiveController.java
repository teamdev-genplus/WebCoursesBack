package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.LiveANDShort.Detail.LiveDetailDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveHomeDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveSimpleCardDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.ShortThumbDTO;
import com.aecode.webcoursesback.services.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveService liveService;
/*
//    // Home (destacados + próximos completos + pasados primeras 6 + shorts primeras N)
//    @GetMapping("/home")
//    public LiveHomeDTO getHome() {
//        return liveService.getHome();
//    }
//
//    // Detalle de un live
//    @GetMapping("/{id}")
//    public LiveDetailDTO getDetail(@PathVariable Long id) {
//        return liveService.getDetail(id);
//    }
//
//    // Próximos: SIEMPRE TODOS (sin paginación)
//    @GetMapping("/upcoming")
//    public List<LiveSimpleCardDTO> getAllUpcoming() {
//        return liveService.getAllUpcoming();
//    }
//
//    // Pasados: paginado para "Ver más" (2 filas x 3 = 6 por defecto)
//    @GetMapping("/past")
//    public Page<LiveSimpleCardDTO> getPast(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "6") int size
//    ) {
//        return liveService.getPast(PageRequest.of(page, size));
//    }
//
//    // Shorts para Home: lista “deslizable” (sin paginación, límite fijo)
//    @GetMapping("/shorts/home")
//    public List<ShortThumbDTO> getShortsForHome(
//            @RequestParam(defaultValue = "20") int limit // ajusta el límite que gustes
//    ) {
//        return liveService.getShortsForHome(limit);
//    }
//
//    // (Opcional futuro) Shorts paginados si alguna vez agregas "Ver más shorts"
//    @GetMapping("/shorts")
//    public Page<ShortThumbDTO> getShorts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        return liveService.getShorts(PageRequest.of(page, size));
//    }

    */
}
