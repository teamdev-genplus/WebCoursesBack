package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserModuleRepo;
import com.aecode.webcoursesback.services.IModuleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
// AGREGA este import
import org.springframework.transaction.annotation.Transactional;




import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleServiceImp implements IModuleService {

    @Autowired
    private IModuleRepo mR;

    @Autowired
    private IUserModuleRepo userModuleAccessRepo;

    @Autowired
    private final ModelMapper modelMapper = new ModelMapper();


    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;

    @Override
    @Transactional
    public void setAssistantApiKey(Long moduleId, String rawKeyOrNull) {
        Module m = mR.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        if (rawKeyOrNull == null || rawKeyOrNull.isBlank()) {
            // limpiar
            m.setAssistantApiKeyEnc(null);
        } else {
            m.setAssistantApiKeyEnc(encryptor.encrypt(rawKeyOrNull.trim()));
        }
        mR.save(m);
    }

    @Override
    @Transactional(readOnly = true)
    public String getAssistantApiKeyMasked(Long moduleId) {
        Module m = mR.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));
        if (m.getAssistantApiKeyEnc() == null || m.getAssistantApiKeyEnc().isBlank()) return null;
        try {
            String raw = encryptor.decrypt(m.getAssistantApiKeyEnc());
            if (raw.length() <= 6) return "***";
            return raw.substring(0, 3) + "****" + raw.substring(raw.length() - 3);
        } catch (Exception e) {
            return "(invalid)";
        }
    }
    @Override
    public List<Module> listAll() {
        return mR.findAll();
    }

    @Override
    public void delete(Long courseId) {
        mR.deleteById(courseId);
    }

    @Override
    public Module listId(Long moduleId) {
        return mR.findById(moduleId).orElse(new Module());
    }

    //Este método obtiene el detalle completo de un módulo específico identificado por su moduleId
    @Override
    public ModuleDTO getModuleDetailById(Long moduleId) {
        Module module = mR.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Modulo no encontrado"));
        if (module.getCourse() == null) {
            throw new IllegalArgumentException("El módulo no está asociado a ningún curso");
        }

        // Mapear entidad a DTO completo
        ModuleDTO moduleDTO = modelMapper.map(module, ModuleDTO.class);

        // Obtener lista de módulos del curso para navegación
        List<Module> courseModules = mR.findByCourse_CourseIdOrderByOrderNumberAsc(module.getCourse().getCourseId());
        moduleDTO.setCourseModules(mapToModuleListDTOs(courseModules));

        return moduleDTO;
    }

    //Este método obtiene el primer módulo de un curso dado, identificado por courseId
    @Override
    public CourseModuleViewDTO getCourseAndFirstModule(Long courseId) {
        // Obtener curso (para información estática)
        Module firstModule = mR.findByCourse_CourseIdOrderByOrderNumberAsc(courseId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el primer módulo del curso"));

        Course course = firstModule.getCourse();

        CourseInfoDTO courseInfo = CourseInfoDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .tagcourse(course.getTagcourse())
                .titledescription(course.getTitledescription())
                .description(course.getDescription())
                .namebuttoncommunity(course.getNamebuttoncommunity())
                .urlbuttoncommunity(course.getUrlbuttoncommunity())
                .availableorlaunching(course.getAvailableorlaunching())
                .urlbrochure(course.getUrlbrochure())
                .highlightImage(course.getHighlightImage())
                .urlnamecourse(course.getUrlnamecourse())
                .type(course.getType())
                .fullprice(course.getFullprice())
                .pricewithdiscount(course.getPricewithdiscount())
                .build();

        ModuleDTO moduleDTO = modelMapper.map(firstModule, ModuleDTO.class);

        List<Module> modules = mR.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        moduleDTO.setCourseModules(mapToModuleListDTOs(modules));

        return CourseModuleViewDTO.builder()
                .course(courseInfo)
                .module(moduleDTO)
                .build();
    }

    // Métodos privados para mapear entidades a DTOs

    private List<ModuleListDTO> mapToModuleListDTOs(List<Module> modules) {
        return modules.stream()
                .map(m -> ModuleListDTO.builder()
                        .moduleId(m.getModuleId())
                        .courseId(m.getCourse().getCourseId())
                        .programTitle(m.getProgramTitle())
                        .orderNumber(m.getOrderNumber())
                        .priceRegular(m.getPriceRegular())
                        .promptPaymentPrice(m.getPromptPaymentPrice())
                        .isOnSale(m.getIsOnSale())
                        .build())
                .collect(Collectors.toList());
    }


}
