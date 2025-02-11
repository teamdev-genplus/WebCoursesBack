package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolDTO {

    private Integer toolId;
    private String name;
    private String picture;

}
