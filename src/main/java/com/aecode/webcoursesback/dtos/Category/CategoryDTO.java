package com.aecode.webcoursesback.dtos.Category;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryDTO {
    private Long categoryId;
    private String name;
}