package com.aecode.webcoursesback.servicesimplement.support;

import com.aecode.webcoursesback.dtos.support.*;
import com.aecode.webcoursesback.dtos.support.admin.*;
import com.aecode.webcoursesback.entities.support.SupportGuidePage;
import com.aecode.webcoursesback.repositories.support.SupportGuidePageRepository;
import com.aecode.webcoursesback.services.support.SupportGuideService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportGuideServiceImpl implements SupportGuideService {

    private final SupportGuidePageRepository repo;
    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private OffsetDateTime toLima(OffsetDateTime utc) {
        return utc == null ? null : utc.atZoneSameInstant(LIMA).toOffsetDateTime();
    }

    /* ================= FRONT ================= */

    @Override
    @Transactional(readOnly = true)
    public List<SupportGuideIndexDTO> publicIndex() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(SupportGuidePage::getId))
                .map(e -> SupportGuideIndexDTO.builder()
                        .id(e.getId())
                        .slug(e.getSlug())
                        .title(e.getTitle())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupportGuidePageViewDTO getPage(String slug, String videoKeyHint) {
        var e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Guía no encontrada: " + slug));

        var ordered = e.getVideos() == null ? List.<SupportGuidePage.VideoItem>of()
                : e.getVideos().stream()
                .sorted(Comparator.comparing(v -> v.getPosition() == null ? 9999 : v.getPosition()))
                .toList();

        // cards (derecha)
        var cards = ordered.stream()
                .map(v -> VideoCardDTO.builder()
                        .key(v.getKey())
                        .title(v.getTitle())
                        .thumbnailUrl(v.getThumbnailUrl())
                        .durationLabel(v.getDurationLabel())
                        .position(v.getPosition())
                        .build())
                .toList();

        // initial video: 1) por videoKeyHint si viene; 2) primer 'position'
        SupportGuidePage.VideoItem init = null;
        if (videoKeyHint != null && !videoKeyHint.isBlank()) {
            init = ordered.stream()
                    .filter(v -> v.getKey() != null && v.getKey().equalsIgnoreCase(videoKeyHint))
                    .findFirst().orElse(null);
        }
        if (init == null) {
            init = ordered.stream().findFirst().orElse(null);
        }



        var initialVideo = init == null ? null : toVideoDetail(init);

        return SupportGuidePageViewDTO.builder()
                .id(e.getId())
                .slug(e.getSlug())
                .title(e.getTitle())
                .intro(e.getIntro())
                .cards(cards)
                .initialVideo(initialVideo)
                .createdAt(toLima(e.getCreatedAt()))
                .updatedAt(toLima(e.getUpdatedAt()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VideoDetailDTO getVideo(String slug, String videoKey) {
        var e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Guía no encontrada: " + slug));

        var v = e.getVideos().stream()
                .filter(it -> it.getKey() != null && it.getKey().equalsIgnoreCase(videoKey))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Video no encontrado: " + videoKey));

        return toVideoDetail(v);
    }

    private VideoDetailDTO toVideoDetail(SupportGuidePage.VideoItem v) {
        var faqs = v.getFaqs() == null ? List.<FaqDTO>of()
                : v.getFaqs().stream()
                .map(f -> FaqDTO.builder().question(f.getQuestion()).answer(f.getAnswer()).build())
                .toList();

        return VideoDetailDTO.builder()
                .key(v.getKey())
                .title(v.getTitle())
                .description(v.getDescription())
                .videoUrl(v.getVideoUrl())
                .durationLabel(v.getDurationLabel())
                .faqs(faqs)
                .build();
    }

    /* ================= ADMIN ================= */

    @Override
    public SupportGuidePageViewDTO upsert(SupportGuideUpsertDTO dto) {
        if (dto.getSlug() == null || dto.getSlug().isBlank())
            throw new IllegalArgumentException("slug requerido");

        var e = repo.findBySlug(dto.getSlug()).orElseGet(SupportGuidePage::new);
        e.setSlug(dto.getSlug());
        e.setTitle(dto.getTitle());
        e.setIntro(dto.getIntro());
        e.setVideos(mapVideosAdmin(dto.getVideos()));

        repo.save(e);
        return getPage(e.getSlug(), null);
    }

    @Override
    public SupportGuidePageViewDTO patchHeader(Long id, UpdateHeaderDTO dto) {
        var e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guía no encontrada (id=" + id + ")"));
        if (dto.getTitle() != null) e.setTitle(dto.getTitle());
        if (dto.getIntro() != null) e.setIntro(dto.getIntro());
        repo.save(e);
        return getPage(e.getSlug(), null);
    }

    @Override
    public SupportGuidePageViewDTO patchVideos(Long id, UpdateVideosDTO dto) {
        var e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guía no encontrada (id=" + id + ")"));
        e.setVideos(mapVideosAdmin(dto.getVideos()));
        repo.save(e);
        return getPage(e.getSlug(), null);
    }

    /* ===== helpers ===== */

    private List<SupportGuidePage.VideoItem> mapVideosAdmin(List<SupportGuideUpsertDTO.VideoAdmin> src) {
        if (src == null) return List.of();
        return src.stream()
                .sorted(Comparator.comparing(v -> v.getPosition() == null ? 9999 : v.getPosition()))
                .map(v -> SupportGuidePage.VideoItem.builder()
                        .key(v.getKey())
                        .position(v.getPosition())
                        .title(v.getTitle())
                        .description(v.getDescription())
                        .thumbnailUrl(v.getThumbnailUrl())
                        .videoUrl(v.getVideoUrl())
                        .durationLabel(v.getDurationLabel())
                        .faqs(v.getFaqs() == null ? List.of() :
                                v.getFaqs().stream()
                                        .map(f -> SupportGuidePage.FaqItem.builder()
                                                .question(f.getQuestion())
                                                .answer(f.getAnswer())
                                                .build())
                                        .toList())
                        .build())
                .toList();
    }
}
