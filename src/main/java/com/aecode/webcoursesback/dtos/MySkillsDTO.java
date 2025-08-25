package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MySkillsDTO {
    private int tagId;
    private String tagName;
    // Nuevo: indica si el usuario ya "ganó" esta skill (terminó al menos un módulo que tenga este tag)
    private boolean achieved;
}
