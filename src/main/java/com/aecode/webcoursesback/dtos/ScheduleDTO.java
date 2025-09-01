package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long scheduleId;
    private String scheduleName;

    // NUEVO: datos estructurados (opcionales)
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String timezone;

}
