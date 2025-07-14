package com.microservice.project.auth.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient updateCustomer(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(String.format("http://%s:%s/profile/update", "localhost", "8091"))
                .build();
    }

}
