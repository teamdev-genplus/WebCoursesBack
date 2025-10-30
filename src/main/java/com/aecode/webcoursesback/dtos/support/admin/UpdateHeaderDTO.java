package com.aecode.webcoursesback.dtos.support.admin;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateHeaderDTO {
    private String title;
    private String intro;
}