package com.ysk.cms.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        initBucket(client);
        return client;
    }

    private void initBucket(MinioClient client) {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("MinIO 버킷 '{}' 생성 완료", bucketName);
            } else {
                log.info("MinIO 버킷 '{}' 이미 존재", bucketName);
            }
        } catch (Exception e) {
            log.warn("MinIO 버킷 초기화 실패: {}. MinIO 서버가 실행 중인지 확인하세요.", e.getMessage());
        }
    }
}
