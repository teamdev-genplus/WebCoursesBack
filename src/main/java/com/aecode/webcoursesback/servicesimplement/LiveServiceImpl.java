package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.LiveANDShort.Detail.InstructorBriefDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Detail.LiveDetailDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Detail.LiveRecommendationDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Detail.ShortRecommendationDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveFeaturedCardDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveHomeDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveSimpleCardDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.ShortThumbDTO;
import com.aecode.webcoursesback.entities.Instructor;
import com.aecode.webcoursesback.entities.LiveANDShort.LiveEvent;
import com.aecode.webcoursesback.entities.LiveANDShort.ShortVideo;
import com.aecode.webcoursesback.entities.Tag;
import com.aecode.webcoursesback.repositories.LiveANDShort.LiveEventRepository;
import com.aecode.webcoursesback.repositories.LiveANDShort.ShortVideoRepository;
import com.aecode.webcoursesback.services.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiveServiceImpl implements LiveService {
    private final LiveEventRepository liveEventRepository;
    private final ShortVideoRepository shortVideoRepository;

    @Override
    public LiveHomeDTO getHome() {
        LocalDateTime now = LocalDateTime.now();

        var featured = liveEventRepository.findByActiveTrueAndHighlightedTrueOrderByHighlightOrderAsc();
        var upcoming = liveEventRepository.findUpcoming(now); // todos
        var pastPage = liveEventRepository.findPast(now, PageRequest.of(0, 6)); // primeras 2x3
        var shorts = shortVideoRepository.findTopNActiveOrderByPublishedAtDesc(20); // método nuevo (ver repo)

        return new LiveHomeDTO(
                featured.stream().map(this::toFeaturedCard).toList(),
                upcoming.stream().map(this::toSimpleCard).toList(),
                pastPage.getContent().stream().map(this::toSimpleCard).toList(),
                shorts.stream().map(this::toShortThumb).toList()
        );
    }

    @Override
    public LiveDetailDTO getDetail(Long liveId) {
        LiveEvent live = liveEventRepository.findById(liveId)
                .orElseThrow(() -> new NoSuchElementException("Live no encontrado"));

        LocalDateTime now = LocalDateTime.now();
        boolean hasStarted = !live.getStartDateTime().isAfter(now);

        String primaryLabel;
        String primaryUrl;

        if (!hasStarted) {
            primaryLabel = "Inscribirse";
            primaryUrl = live.getRegistrationUrl();
        } else {
            if (live.getPlaybackUrl() != null && !live.getPlaybackUrl().isBlank()) {
                primaryLabel = "Ver ahora";
                primaryUrl = live.getPlaybackUrl();
            } else {
                primaryLabel = "NO DISPONIBLE POR EL MOMENTO";
                primaryUrl = null;
            }
        }

        List<Integer> tagIds = (live.getTags() == null) ? List.of()
                : live.getTags().stream().map(Tag::getTagId).toList();

        var relatedShorts = tagIds.isEmpty() ? List.<ShortRecommendationDTO>of()
                : shortVideoRepository.findRelatedByTagIds(tagIds)
                .stream().limit(10)
                .map(this::toShortRecommendation).toList();

        var relatedLives = tagIds.isEmpty() ? List.<LiveRecommendationDTO>of()
                : liveEventRepository.findRelatedByTagIds(tagIds, live.getId())
                .stream().limit(10)
                .map(this::toLiveRecommendation).toList();

        return new LiveDetailDTO(
                live.getId(),
                live.getTitle(),
                live.getFeaturedImageUrl(),
                live.getLongDescription(),
                live.getStartDateTime(),
                live.getDurationMinutes(),
                primaryLabel,
                primaryUrl,
                toInstructorBriefList(live.getInstructors()),
                relatedShorts,
                relatedLives
        );
    }

    @Override
    public List<LiveSimpleCardDTO> getAllUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        return liveEventRepository.findUpcoming(now)
                .stream().map(this::toSimpleCard).toList();
    }

    @Override
    public Page<LiveSimpleCardDTO> getPast(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        var page = liveEventRepository.findPast(now, pageable);
        var mapped = page.getContent().stream().map(this::toSimpleCard).toList();
        return new PageImpl<>(mapped, pageable, page.getTotalElements());
    }

    @Override
    public List<ShortThumbDTO> getShortsForHome(int limit) {
        return shortVideoRepository.findTopNActiveOrderByPublishedAtDesc(limit)
                .stream().map(this::toShortThumb).toList();
    }

    @Override
    public Page<ShortThumbDTO> getShorts(Pageable pageable) {
        var page = shortVideoRepository.findActivePaged(pageable);
        var mapped = page.getContent().stream().map(this::toShortThumb).toList();
        return new PageImpl<>(mapped, pageable, page.getTotalElements());
    }

    // ---- Mappers ----

    private LiveFeaturedCardDTO toFeaturedCard(LiveEvent e) {
        return new LiveFeaturedCardDTO(
                e.getId(),
                e.getTitle(),
                e.getFeaturedCardDescription(),
                e.getFeaturedImageUrl(),
                e.getStartDateTime()
        );
    }

    private LiveSimpleCardDTO toSimpleCard(LiveEvent e) {
        return new LiveSimpleCardDTO(
                e.getId(),
                e.getTitle(),
                e.getGeneralCardImageUrl(),
                e.getStartDateTime()
        );
    }

    private ShortThumbDTO toShortThumb(ShortVideo s) {
        return new ShortThumbDTO(
                s.getId(),
                s.getThumbnailUrl(),
                s.getVideoUrl()
        );
    }

    private ShortRecommendationDTO toShortRecommendation(ShortVideo s) {
        return new ShortRecommendationDTO(
                s.getId(),
                s.getTitle(),
                s.getShortDescription(),
                s.getThumbnailUrl(),
                s.getVideoUrl()
        );
    }

    private LiveRecommendationDTO toLiveRecommendation(LiveEvent e) {
        return new LiveRecommendationDTO(
                e.getId(),
                e.getTitle(),
                e.getGeneralCardImageUrl(),
                e.getStartDateTime()
        );
    }

    private List<InstructorBriefDTO> toInstructorBriefList(Set<Instructor> instructors) {
        if (instructors == null) return List.of();
        return instructors.stream().map(i -> {
            InstructorBriefDTO dto = new InstructorBriefDTO();
            dto.setId(i.getInstructorId());            // asumiendo getId()
            dto.setFullName(i.getName()); // asumiendo getFullName()
            dto.setAvatarUrl(i.getPhotoUrl()); // asumiendo getAvatarUrl()
            dto.setSpecialties(i.getSpecialties() != null ? i.getSpecialties() : List.of());
            return dto;
        }).collect(Collectors.toList());
    }
}
