package com.aecode.webcoursesback.dtos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long userId;
    private String clerkId;
    private String fullname;
    private String email;
    private String passwordHash;
    private String rol;
    private String status;

    @JsonIgnore
    private List<UserCourseDTO> usercourseaccess;
    @JsonIgnore
    private List<UserModuleDTO> usermoduleaccess;

}
