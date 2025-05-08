package com.aecode.webcoursesback.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private int userId;
    private String fullname;
    private String email;
    private String passwordHash;
    private String rol;
    private String status;

    private List<UserProgressSessionDTO> userprogresssessions;
    private List<UserProgressRwDTO> userprogressrw;
    private List<UserCourseDTO> usercourseaccess;
    private List<UserModuleDTO> usermoduleaccess;

}
