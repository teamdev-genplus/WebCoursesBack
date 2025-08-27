package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.LiveANDShort.Detail.LiveDetailDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveHomeDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.LiveSimpleCardDTO;
import com.aecode.webcoursesback.dtos.LiveANDShort.Home.ShortThumbDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LiveService {
    LiveHomeDTO getHome();

    LiveDetailDTO getDetail(Long liveId);

    // Próximos: sin paginación
    List<LiveSimpleCardDTO> getAllUpcoming();

    // Pasados: paginado
    Page<LiveSimpleCardDTO> getPast(Pageable pageable);

    // Shorts para Home (sin paginación, límite)
    List<ShortThumbDTO> getShortsForHome(int limit);

    // Shorts paginados (opcional)
    Page<ShortThumbDTO> getShorts(Pageable pageable);
}
