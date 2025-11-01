package com.aecode.webcoursesback.repositories.Landing;

import com.aecode.webcoursesback.entities.Landing.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventParticipantRepository
        extends JpaRepository<EventParticipant, Long>, JpaSpecificationExecutor<EventParticipant> {

    // Compatibles con front ya existente:
    List<EventParticipant> findByLandingSlugAndBuyerClerkIdAndGroupId(String landingSlug, String buyerClerkId, String groupId);
    List<EventParticipant> findByLandingSlugAndGroupId(String landingSlug, String groupId);

    // Atajos por comprador (se siguen usando en algunos flows)
    List<EventParticipant> findByBuyerClerkIdOrderByCreatedAtDesc(String buyerClerkId);
    List<EventParticipant> findByBuyerClerkIdAndStatusOrderByCreatedAtDesc(String buyerClerkId, EventParticipant.Status status);
    List<EventParticipant> findByBuyerClerkIdAndLandingSlugOrderByCreatedAtDesc(String buyerClerkId, String landingSlug);
    List<EventParticipant> findByBuyerClerkIdAndLandingSlugAndStatusOrderByCreatedAtDesc(String buyerClerkId, String landingSlug, EventParticipant.Status status);

    java.util.Optional<EventParticipant> findFirstByLandingSlugAndBuyerClerkIdAndGroupIdAndParticipantIndex(
            String landingSlug, String buyerClerkId, String groupId, Integer participantIndex
    );
}