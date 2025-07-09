package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.InstructorDTO;
import com.aecode.webcoursesback.entities.Instructor;
import com.aecode.webcoursesback.repositories.IInstructorRepo;
import com.aecode.webcoursesback.services.IInstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorServiceImpl implements IInstructorService {

    @Autowired
    private IInstructorRepo instructorRepo;


    @Override
    public void insert(Instructor instructor) {
        instructorRepo.save(instructor);
    }

    @Override
    public List<Instructor> list() {
        return instructorRepo.findAll();
    }

    @Override
    public void delete(Long instructorId) {
        instructorRepo.deleteById(instructorId);
    }

    @Override
    public Instructor listId(Long instructorId) {
        return instructorRepo.findById(instructorId).orElse(new Instructor());
    }

}
