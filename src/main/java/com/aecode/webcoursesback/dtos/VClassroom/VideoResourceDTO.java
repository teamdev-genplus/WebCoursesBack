package com.aecode.webcoursesback.dtos.VClassroom;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoResourceDTO {
    private Long id;
    private String title;      // name
    private String subtitle;   // subtitle
    private String url;        // url
    private String type;       // "LINK" | "DOWNLOAD"
    private Integer orderNumber;
}
