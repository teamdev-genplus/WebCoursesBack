package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserModuleRepo;
import com.aecode.webcoursesback.services.IModuleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImp implements IModuleService {

    @Autowired
    private IModuleRepo moduleRepo;

    @Autowired
    private IUserModuleRepo userModuleAccessRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void insert(Module module) {
        moduleRepo.save(module);
    }

    @Override
    public List<Module> list() {
        return moduleRepo.findAll();
    }

    @Override
    public void delete(int moduleId) {
        moduleRepo.deleteById(moduleId);
    }

    @Override
    public Module listId(int moduleId) {
        return moduleRepo.findById(moduleId).orElse(null);
    }

    @Override
    public Module findByProgramTitleAndUrlName(String programTitle, String urlName) {
        return moduleRepo.findByProgramTitleAndUrlName(programTitle, urlName);
    }

    @Override
    public Page<ModuleSummaryDTO> listByTags(List<Integer> tagIds, Pageable pageable) {
        Page<Module> modules = moduleRepo.findByTags(tagIds, pageable);
        return modules.map(this::convertToSummaryDTO);
    }

    @Override
    public Page<ModuleSummaryDTO> paginatedList(int offsetOrderNumber, Pageable pageable) {
        Page<Module> modules = moduleRepo.findByOrderNumberGreaterThan(offsetOrderNumber, pageable);
        return modules.map(this::convertToSummaryDTO);
    }

    @Override
    public Page<ModuleSummaryDTO> paginateByMode(Module.Mode mode, Pageable pageable) {
        Page<Module> modules = moduleRepo.findByMode(mode, pageable);
        return modules.map(this::convertToSummaryDTO);
    }

    @Override
    public List<Module> findModulesByUserId(int userId) {
        List<UserModuleAccess> accesses = userModuleAccessRepo.findByUserProfileUserId(userId);
        return accesses.stream()
                .map(UserModuleAccess::getModule)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModuleSummaryDTO> findSummaryModulesByUserId(int userId) {
        List<UserModuleAccess> accesses = userModuleAccessRepo.findByUserProfileUserId(userId);
        List<Module> modules = accesses.stream()
                .map(UserModuleAccess::getModule)
                .collect(Collectors.toList());

        return modules.stream()
                .map(module -> new ModuleSummaryDTO(
                        module.getModuleId(),
                        module.getCourse().getTitle(),
                        module.getProgramTitle(),
                        module.getDescription(),
                        module.getBrochureUrl(),
                        module.getStartDate(),
                        module.getPriceRegular(),
                        module.getIsOnSale(),
                        module.getPrincipalImage(),
                        module.getOrderNumber(),
                        module.getMode(),
                        module.getCertificateHours(),
                        module.getDiscountPercentage(),
                        module.getPromptPaymentPrice(),
                        module.getUrlName(),
                        module.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ModuleSummaryDTO> listByType(String type) {
        List<Module> modules = moduleRepo.findByTypeOrderByOrderNumberAsc(type);
        return modules.stream().map(this::convertToSummaryDTO).collect(Collectors.toList());
    }


    // Conversores

    private ModuleSummaryDTO convertToSummaryDTO(Module module) {
        return ModuleSummaryDTO.builder()
                .moduleId(module.getModuleId())
                .courseTitle(module.getCourse() != null ? module.getCourse().getTitle() : null)
                .programTitle(module.getProgramTitle())
                .description(module.getDescription())
                .brochureUrl(module.getBrochureUrl())
                .startDate(module.getStartDate())
                .certificateHours(module.getCertificateHours())
                .priceRegular(module.getPriceRegular())
                .discountPercentage(module.getDiscountPercentage())
                .promptPaymentPrice(module.getPromptPaymentPrice())
                .isOnSale(module.getIsOnSale())
                .principalImage(module.getPrincipalImage())
                .orderNumber(module.getOrderNumber())
                .mode(module.getMode())
                .urlName(module.getUrlName())
                .build();
    }

    private ModuleDetailDTO convertToDetailDTO(Module module) {
        return ModuleDetailDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getCourse() != null ? module.getCourse().getTitle() : null)
                .programTitle(module.getProgramTitle())
                .description(module.getDescription())
                .brochureUrl(module.getBrochureUrl())
                .whatsappGroupLink(module.getWhatsappGroupLink())
                .startDate(module.getStartDate())
                .certificateHours(module.getCertificateHours())
                .priceRegular(module.getPriceRegular())
                .discountPercentage(module.getDiscountPercentage())
                .promptPaymentPrice(module.getPromptPaymentPrice())
                .isOnSale(module.getIsOnSale())
                .achievement(module.getAchievement())
                .principalImage(module.getPrincipalImage())
                .orderNumber(module.getOrderNumber())
                .mode(module.getMode())
                .urlName(module.getUrlName())
                .benefits(module.getBenefits().stream().map(this::convertToBenefitDTO).collect(Collectors.toList()))
                .tools(module.getTools().stream().map(this::convertToToolDTO).collect(Collectors.toList()))
                .studyPlans(module.getStudyPlans().stream().map(this::convertToStudyPlanDTO).collect(Collectors.toList()))
                .coupons(module.getCoupons().stream().map(this::convertToCouponDTO).collect(Collectors.toList()))
                .freqquests(module.getFreqquests().stream().map(this::convertToFreqQuestDTO).collect(Collectors.toList()))
                .tags(module.getTags().stream().map(this::convertToCourseTagDTO).collect(Collectors.toList()))
                .certificates(module.getCertificates().stream().map(this::convertToCertificateDTO).collect(Collectors.toList()))
                .urlMaterialAccess(module.getUrlMaterialAccess())
                .urlJoinClass(module.getUrlJoinClass())
                .build();
    }

    private MyModuleDTO convertToMyModuleDTO(Module module) {
        return MyModuleDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getCourse() != null ? module.getCourse().getTitle() : null)
                .programTitle(module.getProgramTitle())
                .description(module.getDescription())
                .whatsappGroupLink(module.getWhatsappGroupLink())
                .studyPlans(module.getStudyPlans().stream().map(this::convertToStudyPlanDTO).collect(Collectors.toList()))
                .urlMaterialAccess(module.getUrlMaterialAccess())
                .urlJoinClass(module.getUrlJoinClass())
                .certificates(module.getCertificates().stream().map(this::convertToCertificateDTO).collect(Collectors.toList()))
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

    private moduleBenefitsDTO convertToBenefitDTO(ModuleBenefits benefit) {
        return moduleBenefitsDTO.builder()
                .id(benefit.getId())
                .benefits(benefit.getBenefits())
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
