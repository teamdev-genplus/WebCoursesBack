package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.CourseModuleViewDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Module;

import java.util.List;

public interface IModuleService {
    List<Module> listAll();
    void delete(Long courseId);

    public Module listId(Long moduleId);

    // MODULO DETALLADO (solo cuando curso es de tipo modular)
    ModuleDTO getModuleDetailById(Long moduleId);

    CourseModuleViewDTO getCourseAndFirstModule(Long courseId);
}
