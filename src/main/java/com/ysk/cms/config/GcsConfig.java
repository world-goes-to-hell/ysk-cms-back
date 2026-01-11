package com.ysk.cms.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gcs")
public class GcsConfig {

    private String projectId;
    private String bucketName;

    @Bean
    public Storage storage() {
        log.info("GCS Storage 클라이언트 초기화 - 프로젝트: {}, 버킷: {}", projectId, bucketName);
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }
}
