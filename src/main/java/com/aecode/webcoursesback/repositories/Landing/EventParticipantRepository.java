package com.aecode.webcoursesback.repositories.Landing;

import com.aecode.webcoursesback.entities.Landing.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByLandingSlugAndBuyerClerkIdAndGroupId(String landingSlug, String buyerClerkId, String groupId);
    List<EventParticipant> findByLandingSlugAndBuyerClerkIdAndStatus(String landingSlug, String buyerClerkId, EventParticipant.Status status);
    List<EventParticipant> findByLandingSlugAndGroupId(String landingSlug, String groupId);
    boolean existsByLandingSlugAndGroupIdAndParticipantIndex(String landingSlug, String groupId, Integer participantIndex);
}
