package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.Certificate.UserCertificateDTO;
import java.util.List;


public interface IUserCertificateService {

    List<UserCertificateDTO> getCertificatesByUser(String clerkId);

    UserCertificateDTO addUserCertificate(UserCertificateDTO userCertificateDTO);

    void deleteUserCertificate(Long id);
}
