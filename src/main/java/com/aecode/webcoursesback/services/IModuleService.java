package com.aecode.webcoursesback.services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.aecode.webcoursesback.dtos.ModuleSummaryDTO;
import com.aecode.webcoursesback.entities.Module;

import java.util.List;

public interface IModuleService {
    void insert(Module module);

    List<Module> list();

    void delete(int moduleId);

    Module listId(int moduleId);

    Module findByProgramTitleAndUrlName(String programTitle, String urlName);

    Page<ModuleSummaryDTO> listByTags(List<Integer> tagIds, Pageable pageable);

    Page<ModuleSummaryDTO> paginatedList(int offsetOrderNumber, Pageable pageable);

    Page<ModuleSummaryDTO> paginateByMode(Module.Mode mode, Pageable pageable);

    // Métodos para acceso por usuario (debes implementar según tu modelo de usuarios y acceso)
    List<Module> findModulesByUserId(int userId);

    List<ModuleSummaryDTO> findSummaryModulesByUserId(int userId);

    //LISTAR PARA Cursos PRIMARIOS
    List<ModuleSummaryDTO> listByType(String type);

}
