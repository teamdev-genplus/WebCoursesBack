package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartDTO {
    private int cartId;
    private int userId;
    private Long seccourseId;
    private int moduleId;
}