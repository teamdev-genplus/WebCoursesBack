package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.UserProfileDTO;
import com.aecode.webcoursesback.dtos.UserUpdateDTO;
import com.aecode.webcoursesback.entities.UserDetail;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IUserDetailRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailServiceImp implements IUserDetailService {
    @Autowired
    private IUserDetailRepo udR;
    @Autowired
    private IUserProfileRepository upR;

    @Override
    public List<UserDetail> list() {
        return udR.findAll();
    }

    @Override
    public UserDetail listId(int userD) {
        return udR.findById(userD).orElse(new UserDetail());
    }

    @Override
    public UserDetail update(UserDetail userDetail) {
        return udR.save(userDetail);
    }

    @Override
    public UserDetail findByUserId(int userId) {
        return udR.findByUserId(userId);
    }

    @Override
    public void updateUserDetail(UserUpdateDTO userUpdateDTO) {
// Validar que exista el UserProfile
        UserProfile userProfile = upR.findById(userUpdateDTO.getUserId()).orElse(null);
        if (userProfile == null) {
            throw new RuntimeException("El perfil de usuario no existe");
        }

        UserDetail userDetail = udR.findByUserId(userUpdateDTO.getUserId());

        if (userDetail == null) {
            // Crear un nuevo UserDetail si no existe
            userDetail = new UserDetail();
            userDetail.setUserProfile(userProfile); // relación con el UserProfile
        }

        // En ambos casos (nuevo o existente), se actualizan los campos
        userDetail.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        userDetail.setGender(userUpdateDTO.getGender());
        userDetail.setCountry(userUpdateDTO.getCountry());
        userDetail.setProfession(userUpdateDTO.getProfession());
        userDetail.setEducation(userUpdateDTO.getEducation());
        userDetail.setLinkedin(userUpdateDTO.getLinkedin());
        userDetail.setBirthdate(userUpdateDTO.getBirthdate());

        udR.save(userDetail);

    }


}
