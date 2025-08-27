package com.aecode.webcoursesback.dtos.LiveANDShort.Detail;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class InstructorBriefDTO {
    private Long id;
    private String fullName;
    private String avatarUrl;  // asumiendo campo en Instructor
    private List<String> specialties;
}
