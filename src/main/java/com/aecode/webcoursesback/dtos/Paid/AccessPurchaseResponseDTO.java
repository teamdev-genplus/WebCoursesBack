package com.aecode.webcoursesback.dtos.Paid;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessPurchaseResponseDTO {
    private String purchaseNumber;
    private String clerkId;
    private String email;
    private String fullName;
    private OffsetDateTime purchaseAt;
    private List<UserModuleDTO> grantedModules;
    /** NUEVO: IDs de módulos que se omitieron por ya existir en UserModuleAccess */
    private List<Long> skippedModuleIds;
}
