package com.aecode.webcoursesback.repositories.Business;

import com.aecode.webcoursesback.entities.Business.BusinessLogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessLogoRepository extends JpaRepository<BusinessLogo, Long> {
    List<BusinessLogo> findAllByOrderByIdAsc();
}