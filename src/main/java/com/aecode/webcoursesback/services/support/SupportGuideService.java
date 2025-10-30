package com.aecode.webcoursesback.services.support;

import com.aecode.webcoursesback.dtos.support.*;
import com.aecode.webcoursesback.dtos.support.admin.*;

import java.util.List;

public interface SupportGuideService {

    /* FRONT */
    List<SupportGuideIndexDTO> publicIndex();                          // descubrir slugs
    SupportGuidePageViewDTO getPage(String slug, String videoKeyHint); // page + initial video
    VideoDetailDTO getVideo(String slug, String videoKey);             // cambio dinámico

    /* ADMIN */
    SupportGuidePageViewDTO upsert(SupportGuideUpsertDTO dto);
    SupportGuidePageViewDTO patchHeader(Long id, UpdateHeaderDTO dto);
    SupportGuidePageViewDTO patchVideos(Long id, UpdateVideosDTO dto);
}
