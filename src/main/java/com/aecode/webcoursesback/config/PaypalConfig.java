package com.aecode.webcoursesback.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Configuration
public class PaypalConfig {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    // Configuración del SDK de PayPal
    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode); // Define el entorno (sandbox o live)
        configMap.put("clientId", clientId); // Client ID
        configMap.put("clientSecret", clientSecret); // Client Secret
        return configMap;
    }

    // Credenciales OAuth para obtener el token de acceso
    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    // APIContext que se usará para hacer las peticiones a PayPal
    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        return new APIContext(oAuthTokenCredential().getAccessToken());
    }
}
