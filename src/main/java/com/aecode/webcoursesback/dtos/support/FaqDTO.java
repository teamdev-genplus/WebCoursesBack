package com.aecode.webcoursesback.dtos.support;
import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FaqDTO {
    private String question;
    private String answer;
}
