package com.aecode.webcoursesback.dtos;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChatKeyDTO {
    private String clerkId;
    private String chatKey;
}
