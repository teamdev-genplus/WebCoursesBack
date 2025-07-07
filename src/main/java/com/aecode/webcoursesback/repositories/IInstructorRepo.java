package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInstructorRepo extends JpaRepository<Instructor, Long>{
}
