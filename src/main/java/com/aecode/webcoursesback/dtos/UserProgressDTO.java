package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressDTO {
    private int completedModules;    // módulos con completed = true
    private int inProgressModules;   // módulos otorgados al usuario con completed = false
    private int totalLearningHours;  // suma de horas (asinc + live) SOLO de módulos completados
}
