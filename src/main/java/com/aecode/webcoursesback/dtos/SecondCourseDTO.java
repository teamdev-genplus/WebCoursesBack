package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondCourseDTO {

    private int seccourseId;
    private String title;
    private String programTitle;
    private String description;
    private String module;
    private String brochureUrl;
    private String whatsappGroupLink;
    private String startDate;
    private String certificateHours;
    private Double priceRegular;
    private int discountPercentage;
    private Double promptPaymentPrice;
    private Boolean isOnSale;
    private String achievement;
    private String videoUrl;
    private String principalimage;
    private int totalHours;
    private int numberOfSessions;
    private int numberOfUnits;
    private String[] schedules;
    private String[] requirements;
    @Builder.Default
    private List<String> benefits = new ArrayList<>();
    @Builder.Default
    private List<ToolDTO> tools = new ArrayList<>();
    @Builder.Default
    private List<StudyPlanDTO> studyplans = new ArrayList<>();
    @Builder.Default
    private List<CouponDTO> coupons = new ArrayList<>();
    @Builder.Default
    private List<FreqQuestDTO> freqquests = new ArrayList<>();
    private Mode mode;

    public enum Mode {
        SINCRONO("Síncrono"),
        ASINCRONO("Asíncrono"),
        EN_VIVO("En Vivo");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
