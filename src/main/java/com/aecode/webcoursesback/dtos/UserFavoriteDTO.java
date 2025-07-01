package com.aecode.webcoursesback.dtos;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoriteDTO {
    private Long favoriteId;
    private Long userId;
    private Long courseId;
}
