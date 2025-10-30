package com.aecode.webcoursesback.controllers.support;

import com.aecode.webcoursesback.dtos.support.*;
import com.aecode.webcoursesback.dtos.support.admin.*;
import com.aecode.webcoursesback.services.support.SupportGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/support")
public class SupportGuideController {

    private final SupportGuideService service;

    /* ============= FRONT ============= */

    /** Índice público: descubrir slugs disponibles para rutear la vista */
    @GetMapping("/public-index")
    public ResponseEntity<List<SupportGuideIndexDTO>> publicIndex() {
        return ResponseEntity.ok(service.publicIndex());
    }

    /**
     * Vista pública: entrega header + cards + initialVideo.
     * - initialVideo se elige así:
     *   a) si llega ?videoKey=... → ese
     *   b) si no, el de menor 'position'
     */
    @GetMapping("/{slug}/page")
    public ResponseEntity<SupportGuidePageViewDTO> getPage(
            @PathVariable String slug,
            @RequestParam(name = "videoKey", required = false) String videoKeyHint
    ) {
        return ResponseEntity.ok(service.getPage(slug, videoKeyHint));
    }

    /** Cambio dinámico de video (al hacer click en una card) */
    @GetMapping("/{slug}/videos/{videoKey}")
    public ResponseEntity<VideoDetailDTO> getVideo(
            @PathVariable String slug,
            @PathVariable String videoKey
    ) {
        return ResponseEntity.ok(service.getVideo(slug, videoKey));
    }

    /* ============= ADMIN ============= */

    /** Crear/Reemplazar la página completa */
    @PostMapping("/admin")
    public ResponseEntity<SupportGuidePageViewDTO> upsert(@RequestBody SupportGuideUpsertDTO dto) {
        return ResponseEntity.ok(service.upsert(dto));
    }

    /** PATCH header (título/intro) por ID */
    @PatchMapping("/admin/{id}/header")
    public ResponseEntity<SupportGuidePageViewDTO> patchHeader(
            @PathVariable Long id,
            @RequestBody UpdateHeaderDTO dto
    ) {
        return ResponseEntity.ok(service.patchHeader(id, dto));
    }

    /** PATCH videos (reemplaza la lista completa ordenada) por ID */
    @PatchMapping("/admin/{id}/videos")
    public ResponseEntity<SupportGuidePageViewDTO> patchVideos(
            @PathVariable Long id,
            @RequestBody UpdateVideosDTO dto
    ) {
        return ResponseEntity.ok(service.patchVideos(id, dto));
    }
}
