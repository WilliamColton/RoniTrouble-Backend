package org.roni.ronitrouble.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MilvusConfig {

    @Bean
    public MilvusClientV2 buildMilvusClient() {
        ConnectConfig config = ConnectConfig.builder()
                .uri("http://43.133.38.194:19530")
                .username("root")
                .password("Milvus")
                .dbName("ronitrouble")
                .build();
        return new MilvusClientV2(config);
    }

}
