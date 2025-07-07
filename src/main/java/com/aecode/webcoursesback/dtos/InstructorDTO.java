package com.aecode.webcoursesback.dtos;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorDTO {
    private Long instructorId;
    private String name;
    private String photoUrl;
    private List<String> specialties;
}
