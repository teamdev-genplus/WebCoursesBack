package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.InstructorDTO;

import java.util.List;

public interface IInstructorService {
    List<InstructorDTO> getInstructorsByModuleId(Long moduleId);

    InstructorDTO getInstructorById(Long instructorId);

    InstructorDTO saveInstructor(InstructorDTO instructorDTO);

    void deleteInstructor(Long instructorId);
}
