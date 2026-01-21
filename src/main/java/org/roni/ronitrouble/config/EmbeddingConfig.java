package org.roni.ronitrouble.config;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {

    @Bean
    TextEmbedding buildTextEmbedding() {
        return new TextEmbedding();
    }

}
