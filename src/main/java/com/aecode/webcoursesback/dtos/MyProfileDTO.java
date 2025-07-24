package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyProfileDTO {
    // Información personal
    private String fullname;
    private String phoneNumber;
    private String education;
    private String email;
    private String country;
    private LocalDate birthdate;

    // Progreso
    private UserProgressDTO progress;

    // Habilidades obtenidas
    private List<MySkillsDTO> skills;

    // Certificados obtenidos
    private List<MyCertificateDTO> certificates;
}
