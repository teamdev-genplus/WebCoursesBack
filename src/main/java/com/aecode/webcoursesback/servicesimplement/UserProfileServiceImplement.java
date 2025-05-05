package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.LoginDTO;
import com.aecode.webcoursesback.dtos.UserProfileDTO;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileServiceImplement implements IUserProfileService {
    @Autowired
    private IUserProfileRepository upR;
    @Override
    public void insert(UserProfileDTO userdto) {
        if (upR.existsByProfile_email(userdto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setFullname(userdto.getFullname());
        userProfile.setEmail(userdto.getEmail());
        userProfile.setPasswordHash(userdto.getPasswordHash());
        userProfile.setBirthdate(userdto.getBirthdate());
        userProfile.setPhoneNumber(userdto.getPhoneNumber());
        userProfile.setGender(userdto.getGender());
        userProfile.setExperience(userdto.getExperience());
        userProfile.setRol(userdto.getRol() != null ? userdto.getRol() : "user");
        userProfile.setStatus(userdto.getStatus() != null ? userdto.getStatus() : "Activo");
        upR.save(userProfile);
    }

    @Override
    public List<UserProfile> list() {
        return upR.findAll();
    }

    @Override
    public void delete(int userId) {
        upR.deleteById(userId);
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
}
