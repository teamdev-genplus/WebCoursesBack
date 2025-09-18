package com.aecode.webcoursesback.dtos.Certificate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertificateDTO {
    private Long id;
    private Long userId;
    private Long moduleId;
    private String certificateName;
    private String certificateUrl;
}