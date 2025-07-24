package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImplement implements IUserProfileService {
    @Autowired
    private IUserProfileRepository upR;
    @Autowired
    private IUserDetailRepo udR;
    @Autowired
    private IUserCourseRepo userCourseAccessRepo;

    @Autowired
    private IUserModuleRepo userModuleAccessRepo;

    @Autowired
    private IUserCertificateRepo userCertificateRepo;

    @Autowired
    private IModuleRepo moduleRepo;

    @Override
    public void insert(RegistrationDTO dto) {
        if (upR.existsByProfile_email(dto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(dto.getEmail());
        userProfile.setPasswordHash(dto.getPasswordHash());
        userProfile.setFullname(dto.getFullname());
        upR.save(userProfile);

        UserDetail userDetail = new UserDetail();
        userDetail.setUserProfile(userProfile);
        udR.save(userDetail);
    }

    @Override
    public List<UserProfile> list() {
        return upR.findAll();
    }

    @Override
    public void delete(Long userId) {
        UserProfile user = upR.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        upR.delete(user);
    }

    @Override
    public UserProfile listId(Long userId) {
        return upR.findById(userId).orElse(new UserProfile());
    }

    @Override
    public void update(UserProfile userprofile) {
        upR.save(userprofile);
    }

    @Override
    public UserProfile authenticateUser(LoginDTO logindto) {
        UserProfile profile = upR.findByEmail(logindto.getEmail());
        if (profile != null && profile.getPasswordHash().equals(logindto.getPasswordHash())) {
            return profile; // Devolver el perfil solo si las credenciales son válidas
        }
        return null;
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        // Validar que la contraseña actual es correcta
        boolean isCurrentPasswordValid = upR.validateCurrentPassword(userId, currentPassword);
        if (!isCurrentPasswordValid) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Buscar el usuario y actualizar la contraseña
        UserProfile user = upR.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setPasswordHash(newPassword); // Reemplaza esto por encriptación si es necesario
        upR.save(user); // Guarda los cambios
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<UserUpdateDTO> listusers() {

        List<UserProfile> users = upR.findAll();

        return users.stream().map(user -> {
            UserDetail detail = udR.findByUserId(user.getUserId());
            return UserUpdateDTO.builder()
                    .userId(user.getUserId())
                    .fullname(user.getFullname())
                    .email(user.getEmail())
                    .password(user.getPasswordHash())
                    .profilepicture(detail != null ? detail.getProfilepicture() : null)
                    .birthdate(detail != null ? detail.getBirthdate() : null)
                    .phoneNumber(detail != null ? detail.getPhoneNumber() : null)
                    .gender(detail != null ? detail.getGender() : null)
                    .country(detail != null ? detail.getCountry() : null)
                    .profession(detail != null ? detail.getProfession() : null)
                    .education(detail != null ? detail.getEducation() : null)
                    .linkedin(detail != null ? detail.getLinkedin() : null)
                    .rol(user.getRol())
                    .status(user.getStatus())
                    .build();
        }).toList();
    }

    @Override
    public UserUpdateDTO listusersId(Long userId) {
        UserProfile user = upR.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        UserDetail detail = udR.findByUserId(userId);

        return UserUpdateDTO.builder()
                .userId(user.getUserId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password(user.getPasswordHash()) // Nunca se debe retornar la contraseña
                .profilepicture(detail != null ? detail.getProfilepicture() : null)
                .birthdate(detail != null ? detail.getBirthdate() : null)
                .phoneNumber(detail != null ? detail.getPhoneNumber() : null)
                .gender(detail != null ? detail.getGender() : null)
                .country(detail != null ? detail.getCountry() : null)
                .profession(detail != null ? detail.getProfession() : null)
                .education(detail != null ? detail.getEducation() : null)
                .linkedin(detail != null ? detail.getLinkedin() : null)
                .build();
    }

    @Override
    public MyProfileDTO getMyProfile(Long userId) {
        UserProfile user = upR.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UserDetail detail = udR.findByUserId(userId);

        // ===================== INFO PERSONAL =====================
        String fullname = user.getFullname();
        String email = user.getEmail();
        String phone = detail != null ? detail.getPhoneNumber() : null;
        String education = detail != null ? detail.getEducation() : null;
        String country = detail != null ? detail.getCountry() : null;
        LocalDate birthdate = detail != null ? detail.getBirthdate() : null;

        // ===================== PROGRESO =====================
        List<UserModuleAccess> userModules = userModuleAccessRepo
                .findByUserProfile_UserId(userId)
                .stream()
                .filter(UserModuleAccess::isCompleted)
                .collect(Collectors.toList());


        Set<Long> completedCourseIds = new HashSet<>();
        Set<Long> inProgressCourseIds = new HashSet<>();
        int totalHours = 0;

        Map<Long, List<com.aecode.webcoursesback.entities.Module>> courseModulesMap = new HashMap<>();

        // Agrupar módulos por curso
        for (UserModuleAccess uma : userModules) {
            com.aecode.webcoursesback.entities.Module module = uma.getModule();
            Long courseId = module.getCourse().getCourseId();
            courseModulesMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(module);

            // Sumar horas (asinc + live)
            totalHours += (module.getCantHours_asinc() != null ? module.getCantHours_asinc() : 0)
                    + (module.getCantHours_live() != null ? module.getCantHours_live() : 0);
        }

        for (Map.Entry<Long, List<com.aecode.webcoursesback.entities.Module>> entry : courseModulesMap.entrySet()) {
            Long courseId = entry.getKey();
            List<com.aecode.webcoursesback.entities.Module> completedModules = entry.getValue();

            List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
            if (completedModules.size() == allModules.size()) {
                completedCourseIds.add(courseId);
            } else {
                inProgressCourseIds.add(courseId);
            }
        }

        UserProgressDTO progressDTO = UserProgressDTO.builder()
                .completedCourses(completedCourseIds.size())
                .inProgressCourses(inProgressCourseIds.size())
                .totalLearningHours(totalHours)
                .build();

        // ===================== SKILLS (tags de módulos) =====================
        Set<MySkillsDTO> skillSet = new HashSet<>();
        for (UserModuleAccess uma : userModules) {
            List<Tag> tags = uma.getModule().getTags();
            for (Tag tag : tags) {
                skillSet.add(MySkillsDTO.builder()
                        .tagId(tag.getTagId())
                        .tagName(tag.getName())
                        .build());
            }
        }

        // ===================== CERTIFICADOS =====================
        List<UserCertificate> certs = userCertificateRepo.findByUserProfile_UserId(userId);
        List<MyCertificateDTO> certificateDTOs = certs.stream().map(cert ->
                MyCertificateDTO.builder()
                        .certificateName(cert.getCertificateName())
                        .certificateUrl(cert.getCertificateUrl())
                        .build()
        ).toList();

        // ===================== Construcción del DTO final =====================
        return MyProfileDTO.builder()
                .fullname(fullname)
                .email(email)
                .phoneNumber(phone)
                .education(education)
                .country(country)
                .birthdate(birthdate)
                .progress(progressDTO)
                .skills(skillSet.stream().toList())
                .certificates(certificateDTOs)
                .build();
    }

}
