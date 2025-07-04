package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.UserCertificateDTO;
import java.util.List;

public interface IUserCertificateService {

    List<UserCertificateDTO> getCertificatesByUser(Long userId);

    UserCertificateDTO addUserCertificate(UserCertificateDTO userCertificateDTO);

    void deleteUserCertificate(Long id);
}
