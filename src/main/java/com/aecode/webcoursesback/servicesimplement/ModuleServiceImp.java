package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserModuleRepo;
import com.aecode.webcoursesback.services.IModuleService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImp implements IModuleService {

    @Autowired
    private IModuleRepo mR;

    @Autowired
    private IUserModuleRepo userModuleAccessRepo;

    @Autowired
    private final ModelMapper modelMapper = new ModelMapper();


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
