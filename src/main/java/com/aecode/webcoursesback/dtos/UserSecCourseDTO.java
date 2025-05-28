package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSecCourseDTO {
    private int accessId;
    private int userId;
    private Long seccourseId;
    private boolean completed;
}
