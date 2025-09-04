package com.aecode.webcoursesback.integrations;
import com.aecode.webcoursesback.config.IzipayProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IzipayClient {

    private final IzipayProperties props;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate rest = new RestTemplate();

    private String buildCreatePaymentUrl() {
        String base = props.getApiBaseUrl();
        if (base == null || base.isBlank()) {
            base = "https://api.micuentaweb.pe";
        }
        // quitar slashes finales
        base = base.replaceAll("/+$", "");
        return base + "/api-payment/V4/Charge/CreatePayment";
    }

    public String createPaymentAndGetFormToken(Map<String, Object> payload) {
        String url = buildCreatePaymentUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Basic Auth con username:password
        headers.setBasicAuth(props.getUsername(), props.getPassword(), StandardCharsets.UTF_8);

        ResponseEntity<String> resp =
                rest.postForEntity(url, new HttpEntity<>(payload, headers), String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Izipay CreatePayment " + resp.getStatusCode() + ": " + resp.getBody());
        }

        try {
            JsonNode root = mapper.readTree(resp.getBody());
            String formToken = root.path("answer").path("formToken").asText(null);
            if (formToken == null || formToken.isBlank()) {
                throw new IllegalStateException("formToken no presente en respuesta: " + resp.getBody());
            }
            return formToken;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo parsear respuesta Izipay", e);
        }
    }

    public String getPublicKey()  { return props.getPublicKey(); }
    public String getPassword()   { return props.getPassword(); }
    public String getHmacSha256() { return props.getHmacSha256(); }
}