package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleListDTO {
    private Long moduleId;
    private Long courseId;
    private String programTitle;
    private Integer orderNumber;
}
