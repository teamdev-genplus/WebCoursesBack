package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.LoginDTO;
import com.aecode.webcoursesback.dtos.RegistrationDTO;
import com.aecode.webcoursesback.dtos.UserProfileDTO;
import com.aecode.webcoursesback.dtos.UserUpdateDTO;
import com.aecode.webcoursesback.entities.UserDetail;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IUserDetailRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileServiceImplement implements IUserProfileService {
    @Autowired
    private IUserProfileRepository upR;
    @Autowired
    private IUserDetailRepo udR;
    @Override
    public void insert(RegistrationDTO dto) {
        if (upR.existsByProfile_email(dto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(dto.getEmail());
        userProfile.setPasswordHash(dto.getPasswordHash());
        userProfile.setRandomNameIfEmpty();
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
    public void delete(int userId) {
        UserProfile user = upR.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        upR.delete(user);
    }

    @Override
    public UserProfile listId(int userId) {
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
    public void changePassword(int userId, String currentPassword, String newPassword) {
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
                    .password("")
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
    public UserUpdateDTO listusersId(int userId) {
        UserProfile user = upR.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        UserDetail detail = udR.findByUserId(userId);

        return UserUpdateDTO.builder()
                .userId(user.getUserId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password("") // Nunca se debe retornar la contraseña
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
}
