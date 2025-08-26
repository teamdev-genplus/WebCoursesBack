package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserFavoriteRepo extends JpaRepository<UserFavorite, Long> {

    List<UserFavorite> findByUserProfile_ClerkId(String clerkId);

    Optional<UserFavorite> findByUserProfile_ClerkIdAndCourse_CourseId(String clerkId, Long courseId);

    // ✅ Usa este en su lugar
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM UserFavorite uf
        WHERE uf.userProfile = (SELECT u FROM UserProfile u WHERE u.clerkId = :clerkId)
          AND uf.course = (SELECT c FROM Course c WHERE c.courseId = :courseId)
    """)
    int deleteByClerkAndCourse(@Param("clerkId") String clerkId, @Param("courseId") Long courseId);

    // para chequear rápido si ya es favorito (sin traer la fila completa)
    @Query("""
       SELECT COUNT(uf) > 0 FROM UserFavorite uf
       WHERE uf.userProfile.clerkId = :clerkId AND uf.course.courseId = :courseId
    """)
    boolean existsByClerkAndCourse(@Param("clerkId") String clerkId, @Param("courseId") Long courseId);
}
