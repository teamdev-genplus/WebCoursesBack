package com.aecode.webcoursesback.repositories.Business;


import com.aecode.webcoursesback.entities.Business.BusinessDemoRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDemoRequestRepository extends JpaRepository<BusinessDemoRequest, Long> {
}