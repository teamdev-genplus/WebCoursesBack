package com.aecode.webcoursesback.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModulePurchaseDTO {
    private String clerkId;          // Identificador de Clerk
    private String email;            // Email del usuario (para enviar confirmación)
    private List<Long> moduleIds;
}
