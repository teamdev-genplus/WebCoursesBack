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
        List<ModuleListDTO> modulesList = mR.findByCourse_CourseIdOrderByOrderNumberAsc(module.getCourse().getCourseId())
                .stream()
                .map(m -> ModuleListDTO.builder()
                        .moduleId(m.getModuleId())
                        .courseId(m.getCourse().getCourseId())
                        .programTitle(m.getProgramTitle())
                        .orderNumber(m.getOrderNumber())
                        .build())
                .collect(Collectors.toList());

        moduleDTO.setCourseModules(modulesList);

        return moduleDTO;
    }

    @Override
    public ModuleDTO getFirstModuleByCourseId(Long courseId) {
        List<Module> modules = mR.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        if (modules.isEmpty()) {
            throw new EntityNotFoundException("No modules found for course id: " + courseId);
        }
        Module firstModule = modules.get(0);

        ModuleDTO moduleDTO = modelMapper.map(firstModule, ModuleDTO.class);

        // Solo para cursos modulares, agregamos navegación con módulos hermanos
        if ("modular".equalsIgnoreCase(firstModule.getCourse().getType())) {
            List<ModuleListDTO> modulesList = modules.stream()
                    .map(m -> ModuleListDTO.builder()
                            .moduleId(m.getModuleId())
                            .courseId(m.getCourse().getCourseId())
                            .programTitle(m.getProgramTitle())
                            .orderNumber(m.getOrderNumber())
                            .build())
                    .collect(Collectors.toList());
            moduleDTO.setCourseModules(modulesList);
        } else {
            // Para diplomados o simples, no hay navegación entre módulos
            moduleDTO.setCourseModules(List.of());
        }

        return moduleDTO;
    }


}
