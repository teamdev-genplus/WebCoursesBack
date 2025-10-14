package com.aecode.webcoursesback.repositories.Landing;
import com.aecode.webcoursesback.entities.Landing.CallForPresentationSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallForPresentationSubmissionRepository extends JpaRepository<CallForPresentationSubmission, Long> {

    List<CallForPresentationSubmission> findByLandingSlugOrderByCreatedAtDesc(String slug);
    List<CallForPresentationSubmission> findByLandingSlugAndStatusOrderByCreatedAtDesc(
            String slug, CallForPresentationSubmission.Status status);
}