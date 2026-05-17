package com.tengke.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI配置
 */
@Configuration
public class AiConfig {

    @Value("${ai.apiKey}")
    private String apiKey;

    @Bean
    public String aiApiKey() {
        return apiKey;
    }
}
