package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Certificate.UserCertificateDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserCertificate;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserCertificateRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.IUserCertificateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserCertificateServiceImpl implements IUserCertificateService {

    @Autowired
    private IUserCertificateRepo userCertificateRepo;

    @Autowired
    private IUserProfileRepository userProfileRepo;

    @Autowired
    private IModuleRepo moduleRepo;

    @Override
    public List<UserCertificateDTO> getCertificatesByUser(String clerkId) {
        List<UserCertificate> certificates = userCertificateRepo.findByUserProfile_ClerkId(clerkId);
        return certificates.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserCertificateDTO addUserCertificate(UserCertificateDTO dto) {
        UserProfile user = userProfileRepo.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Module module = moduleRepo.findById(dto.getModuleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));

        UserCertificate cert = UserCertificate.builder()
                .userProfile(user)
                .module(module)
                .certificateName(dto.getCertificateName())
                .certificateUrl(dto.getCertificateUrl())
                .build();

        UserCertificate saved = userCertificateRepo.save(cert);

        return mapToDTO(saved);
    }

    @Override
    public void deleteUserCertificate(Long id) {
        userCertificateRepo.deleteById(id);
    }

    private UserCertificateDTO mapToDTO(UserCertificate cert) {
        return UserCertificateDTO.builder()
                .id(cert.getId())
                .userId(cert.getUserProfile().getUserId())
                .moduleId(cert.getModule().getModuleId())
                .certificateName(cert.getCertificateName())
                .certificateUrl(cert.getCertificateUrl())
                .build();
    }
}
