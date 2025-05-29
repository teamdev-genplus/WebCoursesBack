package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSecCoursePurchaseDTO {
    private int userId;
    private List<Long> seccourseIds;  // lista de ids de cursos comprados
}