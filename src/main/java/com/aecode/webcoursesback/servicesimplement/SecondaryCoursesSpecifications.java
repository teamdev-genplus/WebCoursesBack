package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SecondaryCoursesSpecifications {

    public static Specification<SecondaryCourses> hasAttribute(String attribute, Object value) {
        return (Root<SecondaryCourses> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get(attribute), value);
        };
    }
}
