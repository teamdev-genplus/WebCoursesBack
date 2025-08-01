package com.aecode.webcoursesback.dtos.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleAccessDTO {
    private Long moduleId;
    private Long courseId;
    private String programTitle;
    private Integer orderNumber;
    private Boolean hasAccess;
}
