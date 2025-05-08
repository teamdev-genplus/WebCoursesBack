package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.SecondCourseSummaryDTO;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.repositories.ISecondCourseRepo;
import com.aecode.webcoursesback.services.ISecondCourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecondCourseServImp implements ISecondCourseService {
    @Autowired
    private ISecondCourseRepo scR;

    @Override
    public void insert(SecondaryCourses secondcourse) {
        scR.save(secondcourse);
    }

    @Override
    public List<SecondaryCourses> list() {
        return scR.listByOrderNumber();
    }

    @Override
    public void delete(Long secondcourseId) {
        scR.deleteById(secondcourseId);
    }

    @Override
    public SecondaryCourses listId(Long secondcourseId) {
        return scR.findById(secondcourseId).orElse(new SecondaryCourses());
    }

    @Override
    public SecondaryCourses listByModulexProgram(String moduleNumber, String programTitle) {
        return scR.findByModulexProgram(moduleNumber, programTitle);
    }

    public Page<SecondCourseSummaryDTO> paginatedList(int offsetCourseId, Pageable pageable) {
        Page<SecondaryCourses> courses = scR.findByOrderNumberGreaterThan(offsetCourseId, pageable);
        return courses.map(course -> new SecondCourseSummaryDTO(
                course.getSeccourseId(),
                course.getTitle(),
                course.getProgramTitle(),
                course.getDescription(),
                course.getModule(),
                course.getStartDate(),
                course.getCertificateHours(),
                course.getPriceRegular(),
                course.getDiscountPercentage(),
                course.getPromptPaymentPrice(),
                course.getIsOnSale(),
                course.getPrincipalimage(),
                course.getOrderNumber(),
                course.getMode()));
    }

    public Page<SecondCourseSummaryDTO> paginateByMode(String mode, Pageable pageable) {
        SecondaryCourses.Mode modeEnum = SecondaryCourses.Mode.valueOf(mode);

        Page<SecondaryCourses> courses = scR.findByMode(modeEnum, pageable);
        return courses.map(course -> new SecondCourseSummaryDTO(
                course.getSeccourseId(),
                course.getTitle(),
                course.getProgramTitle(),
                course.getDescription(),
                course.getModule(),
                course.getStartDate(),
                course.getCertificateHours(),
                course.getPriceRegular(),
                course.getDiscountPercentage(),
                course.getPromptPaymentPrice(),
                course.getIsOnSale(),
                course.getPrincipalimage(),
                course.getOrderNumber(),
                course.getMode()));
    }

    @Override
    public List<SecondaryCourses> searchByAttribute(String attribute, String value) {
        Specification<SecondaryCourses> spec = SecondaryCoursesSpecifications.hasAttribute(attribute, value);
        return scR.findAll(spec);
    }

    public Page<SecondCourseSummaryDTO> listByCourseTags(List<Integer> tagIds, Pageable pageable) {
        String tagIdsFormatted = "{" + tagIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "}";

        Page<SecondaryCourses> courses = scR.findByCourseTags(tagIdsFormatted, pageable);

        return courses.map(course -> new SecondCourseSummaryDTO(
                course.getSeccourseId(),
                course.getTitle(),
                course.getProgramTitle(),
                course.getDescription(),
                course.getModule(),
                course.getStartDate(),
                course.getCertificateHours(),
                course.getPriceRegular(),
                course.getDiscountPercentage(),
                course.getPromptPaymentPrice(),
                course.getIsOnSale(),
                course.getPrincipalimage(),
                course.getOrderNumber(),
                course.getMode()));
    }
}
