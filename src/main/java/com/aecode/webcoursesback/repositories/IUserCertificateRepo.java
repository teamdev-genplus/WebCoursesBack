package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserCertificateRepo extends JpaRepository<UserCertificate,Long> {
    List<UserCertificate> findByUserProfile_ClerkId(String clerkId);
    Optional<UserCertificate> findByCertificateCodeIgnoreCase(String certificateCode);
}