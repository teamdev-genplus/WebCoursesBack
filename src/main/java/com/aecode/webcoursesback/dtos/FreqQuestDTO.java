package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreqQuestDTO {
    private int freqquestId;
    private String questionText;
    private String answerText;
}
