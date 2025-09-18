package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Certificate.CertificateDetailDTO;
import com.aecode.webcoursesback.dtos.Certificate.CertificateVerifyResponseDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserCertificate;
import com.aecode.webcoursesback.repositories.IUserCertificateRepo;
import com.aecode.webcoursesback.services.ICertificateLookupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CertificateLookupServiceImpl implements ICertificateLookupService {
    private final IUserCertificateRepo repo;

    @Override
    public CertificateVerifyResponseDTO verify(String code) {
        String normalized = normalize(code);
        return repo.findByCertificateCodeIgnoreCase(normalized)
                .map(this::toVerifyResponseOk)
                .orElse(CertificateVerifyResponseDTO.builder()
                        .verified(false)
                        .message("Certificado no encontrado")
                        .certificateCode(code)
                        .build());
    }

    @Override
    public CertificateDetailDTO getDetails(String code) {
        String normalized = normalize(code);
        UserCertificate cert = repo.findByCertificateCodeIgnoreCase(normalized)
                .orElseThrow(() -> new EntityNotFoundException("Certificado no encontrado"));

        return toDetailDTO(cert);
    }

    // ================= helpers =================

    private String normalize(String code) {
        return code == null ? "" : code.trim();
    }

    private CertificateVerifyResponseDTO toVerifyResponseOk(UserCertificate c) {
        String title = resolveTitle(c);
        Integer hours = resolveDurationHours(c);
        return CertificateVerifyResponseDTO.builder()
                .verified(true)
                .message("Certificado verificado exitosamente")
                .certificateCode(c.getCertificateCode())
                .studentFullName(safeFullName(c))
                .courseOrModuleTitle(title)
                .issuedAt(c.getIssuedAt())
                .durationHours(hours)
                .build();
    }

    private CertificateDetailDTO toDetailDTO(UserCertificate c) {
        boolean isCourse = c.getCourse() != null;
        String title = resolveTitle(c);
        String description = resolveDescriptionByCertificate(c);

        return CertificateDetailDTO.builder()
                .certificateCode(c.getCertificateCode())
                .studentFullName(safeFullName(c))
                .entityType(isCourse ? "COURSE" : "MODULE")
                .courseId(isCourse ? c.getCourse().getCourseId() : null)
                .moduleId(!isCourse && c.getModule() != null ? c.getModule().getModuleId() : null)
                .title(title)
                .descriptionByCertificate(description)
                .issuedAt(c.getIssuedAt())
                .certificateImage(c.getCertificateImage())
                .certificateUrl(c.getCertificateUrl())
                .build();
    }

    private String safeFullName(UserCertificate c) {
        if (c.getUserProfile() == null) return null;
        // Usa el nombre del perfil; si no tiene, cae en email
        String fullname = c.getUserProfile().getFullname();
        if (fullname != null && !fullname.isBlank()) return fullname;
        return c.getUserProfile().getEmail();
    }

    private String resolveTitle(UserCertificate c) {
        Course course = c.getCourse();
        Module module = c.getModule();

        if (course != null) {
            return course.getTitle();
        }
        if (module != null) {
            // según tu requerimiento: usar titleStudyplan para módulo
            return module.getTitleStudyplan();
        }
        return c.getCertificateName(); // fallback
    }

    private String resolveDescriptionByCertificate(UserCertificate c) {
        Course course = c.getCourse();
        Module module = c.getModule();

        if (course != null) {
            return course.getDescriptionbyCertificate();
        }
        if (module != null) {
            return module.getDescriptionbyCertificate();
        }
        return null;
    }

    private Integer resolveDurationHours(UserCertificate c) {
        Course course = c.getCourse();
        Module module = c.getModule();

        if (course != null) {
            // de tu entity Course: cantTotalHours parece el indicador para filtro
            return course.getCantTotalHours();
        }
        if (module != null) {
            // de tu entity Module: totalHours
            return module.getTotalHours();
        }
        return null;
    }
}
