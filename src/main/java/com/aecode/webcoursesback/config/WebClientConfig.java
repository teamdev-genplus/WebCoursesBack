package com.aecode.webcoursesback.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient izipayWebClient(IzipayProperties props) {
        return WebClient.builder()
                .baseUrl(props.getApiBaseUrl())
                .build();
    }
}