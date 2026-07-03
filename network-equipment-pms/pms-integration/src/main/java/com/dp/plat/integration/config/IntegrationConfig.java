package com.dp.plat.integration.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Integration module configuration. Registers the {@link RestTemplate} used by
 * the D365/FP adapters and enables the {@code @ConfigurationProperties} beans.
 */
@Configuration
@EnableConfigurationProperties({D365Properties.class, FpProperties.class,
        IntegrationProperties.class, OaProperties.class})
public class IntegrationConfig {

    /**
     * Shared {@link RestTemplate} with sensible connect/read timeouts for
     * external system calls.
     */
    @Bean
    public RestTemplate integrationRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
