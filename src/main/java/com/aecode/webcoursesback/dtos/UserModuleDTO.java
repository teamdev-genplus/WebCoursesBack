package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModuleDTO {
    private Long accessId;
    private String clerkId;
    private Long moduleId;
    private boolean completed;
}
