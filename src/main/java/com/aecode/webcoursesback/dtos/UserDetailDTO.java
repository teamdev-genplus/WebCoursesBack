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
public class UserDetailDTO {
    private int detailsId;
    private int userId;
    private String profilepicture;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthdate;
    private String phoneNumber;
    private String gender;

    private String country;
    private String profession;
    private String education;
    private String linkedin;
}
