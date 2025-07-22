package com.aecode.webcoursesback.dtos;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseModuleViewDTO {
    private CourseInfoDTO course;
    private ModuleDTO module;
}
