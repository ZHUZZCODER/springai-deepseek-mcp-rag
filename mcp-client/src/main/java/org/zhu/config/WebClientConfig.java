package org.zhu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter((request, next) -> {
                    System.out.println("Request URL: " + request.url());
                    System.out.println("Request Headers: " + request.headers());
                    return next.exchange(request);
                });
    }
}