package com.example.common.files;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.oss")
public class OSSProperties {

    private String endpoint;

    private String region;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    /**
     * 路径前缀，加在 endPoint 之后
     */
    private String pathPrefix;  //ojplatform
}