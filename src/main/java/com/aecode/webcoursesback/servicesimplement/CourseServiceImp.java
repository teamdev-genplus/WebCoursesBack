package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.ICourseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class CourseServiceImp implements ICourseService {
    @Autowired
    private ICourseRepo courseRepo;

    @Autowired
    private IUserCourseRepo userCourseAccessRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void insert(Course course) {
        courseRepo.save(course);
    }

    @Override
    public List<Course> listAll() {
        return courseRepo.listByOrderNumber();
    }

    @Override
    public void delete(int courseId) {
        courseRepo.deleteById(courseId);
    }

    @Override
    public Course getById(int courseId) {
        return courseRepo.findById(courseId).orElse(null);
    }

    @Override
    public Page<CourseSummaryDTO> paginatedList(int offset, Pageable pageable) {
        Page<Course> courses = courseRepo.findByOrderNumberGreaterThan(offset, pageable);
        return courses.map(this::convertToSummaryDTO);
    }

    @Override
    public Page<CourseSummaryDTO> paginateByMode(String mode, Pageable pageable) {
        Course.Mode modeEnum = Course.Mode.valueOf(mode.toUpperCase());
        Page<Course> courses = courseRepo.findByMode(modeEnum, pageable);
        return courses.map(this::convertToSummaryDTO);
    }

    @Override
    public Page<CourseSummaryDTO> listByTags(List<Integer> tagIds, Pageable pageable) {
        String tagIdsFormatted = "{" + tagIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "}";
        Page<Course> courses = courseRepo.findByCourseTags(tagIdsFormatted, pageable);
        return courses.map(this::convertToSummaryDTO);
    }

    @Override
    public List<Course> findCoursesByUserId(int userId) {
        List<UserCourseAccess> accesses = userCourseAccessRepo.findByUserProfileUserId(userId);
        return accesses.stream()
                .map(UserCourseAccess::getCourse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDetailDTO getCourseDetail(int courseId) {
        Course course = getById(courseId);
        if (course == null) return null;
        return convertToDetailDTO(course);
    }

    @Override
    public MyCourseDTO getMyCourse(int userId, int courseId) {
        boolean hasAccess = userCourseAccessRepo.existsByUserProfileUserIdAndCourseCourseId(userId, courseId);
        if (!hasAccess) return null;
        Course course = getById(courseId);
        if (course == null) return null;
        return convertToMyCourseDTO(course);
    }

    @Override
    public List<Course> searchByAttribute(String attribute, String value) {
        Specification<Course> spec = (root, query, cb) -> cb.like(cb.lower(root.get(attribute)), "%" + value.toLowerCase() + "%");
        return courseRepo.findAll(spec);
    }

    // Conversores

    private CourseSummaryDTO convertToSummaryDTO(Course course) {
        return CourseSummaryDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .programTitle(course.getProgramTitle())
                .description(course.getDescription())
                .module(course.getModule())
                .startDate(course.getStartDate())
                .certificateHours(course.getCertificateHours())
                .priceRegular(course.getPriceRegular())
                .discountPercentage(course.getDiscountPercentage())
                .promptPaymentPrice(course.getPromptPaymentPrice())
                .isOnSale(course.getIsOnSale())
                .principalImage(course.getPrincipalImage())
                .orderNumber(course.getOrderNumber())
                .mode(course.getMode())
                .urlName(course.getUrlName())
                .build();
    }

    private CourseDetailDTO convertToDetailDTO(Course course) {
        return CourseDetailDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .programTitle(course.getProgramTitle())
                .description(course.getDescription())
                .module(course.getModule())
                .brochureUrl(course.getBrochureUrl())
                .whatsappGroupLink(course.getWhatsappGroupLink())
                .startDate(course.getStartDate())
                .certificateHours(course.getCertificateHours())
                .priceRegular(course.getPriceRegular())
                .discountPercentage(course.getDiscountPercentage())
                .promptPaymentPrice(course.getPromptPaymentPrice())
                .isOnSale(course.getIsOnSale())
                .achievement(course.getAchievement())
                .principalImage(course.getPrincipalImage())
                .orderNumber(course.getOrderNumber())
                .mode(course.getMode())
                .urlName(course.getUrlName())
                .benefits(course.getBenefits())
                .tools(course.getTools().stream().map(this::convertToToolDTO).collect(Collectors.toList()))
                .studyPlans(course.getStudyPlans().stream().map(this::convertToStudyPlanDTO).collect(Collectors.toList()))
                .coupons(course.getCoupons().stream().map(this::convertToCouponDTO).collect(Collectors.toList()))
                .freqquests(course.getFreqquests().stream().map(this::convertToFreqQuestDTO).collect(Collectors.toList()))
                .tags(course.getTags().stream().map(this::convertToCourseTagDTO).collect(Collectors.toList()))
                .certificates(course.getCertificates().stream().map(this::convertToCertificateDTO).collect(Collectors.toList()))
                .urlMaterialAccess(course.getUrlMaterialAccess())
                .urlJoinClass(course.getUrlJoinClass())
                .build();
    }

    private MyCourseDTO convertToMyCourseDTO(Course course) {
        return MyCourseDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .programTitle(course.getProgramTitle())
                .description(course.getDescription())
                .module(course.getModule())
                .whatsappGroupLink(course.getWhatsappGroupLink())
                .studyPlans(course.getStudyPlans().stream().map(this::convertToStudyPlanDTO).collect(Collectors.toList()))
                .urlMaterialAccess(course.getUrlMaterialAccess())
                .urlJoinClass(course.getUrlJoinClass())
                .certificates(course.getCertificates().stream().map(this::convertToCertificateDTO).collect(Collectors.toList()))
                .build();
    }

    private ToolDTO convertToToolDTO(Tool tool) {
        return ToolDTO.builder()
                .toolId(tool.getToolId())
                .name(tool.getName())
                .picture(tool.getPicture())
                .build();
    }

    private StudyPlanDTO convertToStudyPlanDTO(StudyPlan sp) {
        return StudyPlanDTO.builder()
                .studyplanId(sp.getStudyplanId())
                .unit(sp.getUnit())
                .hours(sp.getHours())
                .sessions(sp.getSessions())
                .orderNumber(sp.getOrderNumber())
                .urlrecording(sp.getUrlrecording())
                .dmaterial(sp.getDmaterial())
                .viewpresentation(sp.getViewpresentation())
                .build();
    }

    private CouponDTO convertToCouponDTO(Coupon coupon) {
        return CouponDTO.builder()
                .couponId(coupon.getCouponId())
                .name(coupon.getName())
                .discount(coupon.getDiscount())
                .build();
    }

    private FreqQuestDTO convertToFreqQuestDTO(FreqQuest fq) {
        return FreqQuestDTO.builder()
                .freqquestId(fq.getFreqquestId())
                .questionText(fq.getQuestionText())
                .answerText(fq.getAnswerText())
                .build();
    }

    private CourseTagDTO convertToCourseTagDTO(CourseTag tag) {
        return CourseTagDTO.builder()
                .courseTagId(tag.getCourseTagId())
                .courseTagName(tag.getCourseTagName())
                .build();
    }

    private CertificateDTO convertToCertificateDTO(Certificate cert) {
        return CertificateDTO.builder()
                .id(cert.getId())
                .name(cert.getName())
                .description(cert.getDescription())
                .url(cert.getUrl())
                .build();
    }


}
