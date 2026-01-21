package org.roni.ronitrouble.config;

import io.agentscope.core.model.OpenAIChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AIConfig {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model}")
    private String modelName;

    @Value("${ai.base-url}")
    private String baseUrl;

    @Bean
    public OpenAIChatModel buildModel() {
        return OpenAIChatModel.builder()
                .apiKey(apiKey)
                .stream(true)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .build();
    }

}