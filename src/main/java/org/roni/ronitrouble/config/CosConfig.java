package org.roni.ronitrouble.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {

    @Value("${cos.secret-id}")
    private String COS_SECRET_ID;

    @Value("${cos.secret-key}")
    private String COS_SECRET_KEY;

    @Value("${cos.region}")
    private String COS_REGION;

    @Bean
    public COSClient buildCosClient() {
        COSCredentials cred = new BasicCOSCredentials(COS_SECRET_ID, COS_SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region(COS_REGION));
        return new COSClient(cred, clientConfig);
    }

}
