package com.example.common.message;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliConfig {
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Bean("AliClient")
    public Client client() throws Exception {
//        System.out.println("AccessKeyId: " + accessKeyId);
//        System.out.println("AccessKeySecret: " + accessKeySecret);
//        System.out.println("Endpoint: " + endpoint);
        Config config = new Config()
                .setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret).setEndpoint(endpoint);
        return new Client(config);
    }
}
