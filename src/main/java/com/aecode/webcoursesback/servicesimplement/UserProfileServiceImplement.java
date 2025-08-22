package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.MyProfileDTO;
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
    public void insertuserClerk(UserClerkDTO dto) {
        if (upR.existsByClerkId(dto.getClerkId())) {
            throw new RuntimeException("El usuario ya existe con ese Clerk ID");
        }

        if (upR.existsByProfile_email(dto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setClerkId(dto.getClerkId());
        userProfile.setEmail(dto.getEmail());
        upR.save(userProfile);
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
            UserDetail detail = udR.findByClerkId(user.getClerkId());
            return UserUpdateDTO.builder()
                    .userId(user.getUserId())
                    .clerkId(user.getClerkId())
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
    public UserUpdateDTO listClerkId(String clerkId) {
        UserProfile user = upR.findByClerkId(clerkId).orElse(null);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + clerkId);
        }

        UserDetail detail = udR.findByClerkId(clerkId);

        return UserUpdateDTO.builder()
                .userId(user.getUserId())
                .clerkId(user.getClerkId())
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
    public MyProfileDTO getMyProfile(String clerkId) {
        UserProfile user = upR.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UserDetail detail = udR.findByClerkId(clerkId);

        // ===================== INFO PERSONAL =====================
        String fullname  = user.getFullname();
        String email     = user.getEmail();
        String phone     = detail != null ? detail.getPhoneNumber() : null;
        String education = detail != null ? detail.getEducation()    : null;
        String country   = detail != null ? detail.getCountry()      : null;
        LocalDate birthdate = detail != null ? detail.getBirthdate() : null;

        // ===================== PROGRESO (por MÓDULOS) =====================
        // Trae TODOS los accesos del usuario (completados y no completados)
        List<UserModuleAccess> allUMAs = userModuleAccessRepo.findByUserProfile_ClerkId(clerkId);

        int completedModules = 0;
        int inProgressModules = 0;
        int totalLearningHours = 0;

        for (UserModuleAccess uma : allUMAs) {
            Module module = uma.getModule();
            if (uma.isCompleted()) {
                completedModules++;
                // Sumar horas SOLO de módulos completados
                if (module != null) {
                    totalLearningHours += nvl(module.getCantHours_asinc()) + nvl(module.getCantHours_live());
                }
            } else {
                inProgressModules++;
            }
        }

        UserProgressDTO progressDTO = UserProgressDTO.builder()
                .completedModules(completedModules)
                .inProgressModules(inProgressModules)
                .totalLearningHours(totalLearningHours)
                .build();

        // ===================== SKILLS (sin duplicados, SOLO de módulos completados) =====================
        Set<Integer> seenTagIds = new HashSet<>();
        List<MySkillsDTO> skills = new ArrayList<>();

        for (UserModuleAccess uma : allUMAs) {
            if (!uma.isCompleted()) continue; // skills solo si el módulo está completado

            Module m = uma.getModule();
            if (m == null) continue;

            List<Tag> tags = Optional.ofNullable(m.getTags()).orElse(Collections.emptyList());
            for (Tag tag : tags) {
                if (tag == null) continue;
                if (seenTagIds.add(tag.getTagId())) {
                    skills.add(MySkillsDTO.builder()
                            .tagId(tag.getTagId())
                            .tagName(tag.getName())
                            .build());
                }
            }
        }

        // ===================== CERTIFICADOS =====================
        List<UserCertificate> certs = userCertificateRepo.findByUserProfile_ClerkId(clerkId);
        List<MyCertificateDTO> certificateDTOs = certs.stream().map(cert ->
                MyCertificateDTO.builder()
                        .certificateName(cert.getCertificateName())
                        .certificateUrl(cert.getCertificateUrl())
                        .build()
        ).toList();

        // ===================== DTO Final =====================
        return MyProfileDTO.builder()
                .fullname(fullname)
                .email(email)
                .phoneNumber(phone)
                .education(education)
                .country(country)
                .birthdate(birthdate)
                .progress(progressDTO)
                .skills(skills)
                .certificates(certificateDTOs)
                .build();
    }

    // Helper para evitar NPE en sumas
    private static int nvl(Integer v) { return v == null ? 0 : v; }


}
