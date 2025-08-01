package com.aecode.webcoursesback.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    private Long userId;
    private String clerkId;
    private String fullname;
    private String email;
    private String password;
    private String profilepicture;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthdate;
    private String phoneNumber;
    private String gender;
    private String country;
    private String profession;
    private String education;
    private String linkedin;
    private String rol;
    private String status;
}
