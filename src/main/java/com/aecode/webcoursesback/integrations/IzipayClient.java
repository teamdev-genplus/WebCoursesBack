package com.aecode.webcoursesback.integrations;
import com.aecode.webcoursesback.config.IzipayProperties;
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
    private final RestTemplate restTemplate = new RestTemplate();

    public String createPaymentAndGetFormToken(Map<String, Object> payload) {
        String url = props.getApiBaseUrl() + "/api-payment/V4/Charge/CreatePayment";

        String basic = props.getUsername() + ":" + props.getPassword();
        String basicBase64 = Base64.getEncoder().encodeToString(basic.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + basicBase64);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("Izipay CreatePayment sin respuesta válida");
        }

        // Respuesta tipo:
        // { status: "SUCCESS", answer: { formToken: "..." }, ... }
        Map body = resp.getBody();
        Object answerObj = body.get("answer");
        if (!(answerObj instanceof Map)) {
            throw new IllegalStateException("Respuesta Izipay sin 'answer'");
        }
        Map answer = (Map) answerObj;
        Object formToken = answer.get("formToken");
        if (formToken == null) {
            throw new IllegalStateException("Respuesta Izipay sin 'formToken'");
        }
        return String.valueOf(formToken);
    }

    public String getPublicKey() {
        return props.getPublicKey();
    }

    public String getHmacSha256() {
        return props.getHmacSha256();
    }

    public String getPassword() {
        return props.getPassword();
    }
}