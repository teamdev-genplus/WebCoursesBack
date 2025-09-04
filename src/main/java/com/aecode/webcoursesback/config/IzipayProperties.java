package com.aecode.webcoursesback.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "izipay")
public class IzipayProperties {
    private String apiBaseUrl;     // https://api.micuentaweb.pe
    private String username;       // 46068555
    private String password;       // testpassword_... o prodpassword_...
    private String publicKey;      // 46068555:testpublickey_...
    private String hmacSha256;     // clave para validar retorno del navegador

}
