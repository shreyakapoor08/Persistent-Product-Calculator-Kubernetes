package com.example.container1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Container1Config {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
