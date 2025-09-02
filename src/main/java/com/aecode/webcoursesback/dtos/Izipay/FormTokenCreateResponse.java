package com.aecode.webcoursesback.dtos.Izipay;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormTokenCreateResponse {
    private String formToken;
    private String publicKey;  // el front la necesita para cargar el script
}
