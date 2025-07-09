package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.InstructorDTO;
import com.aecode.webcoursesback.entities.Instructor;

import java.util.List;

public interface IInstructorService {
    // Inserta un nuevo instructor
    void insert(Instructor instructor);

    // Lista todos los instructores
    List<Instructor> list();

    // Elimina un instructor por ID
    void delete(Long instructorId);

    // Obtiene un instructor por ID
    Instructor listId(Long instructorId);

}
