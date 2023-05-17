package org.jenjetsu.com.core.config;

import io.minio.MinioClient;
import org.jenjetsu.com.core.service.implementation.MinioServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class MinioConfiguration {

    private final String url;
    private final String accessKey;
    private final String secreteKey;
    private final boolean secure;
    private final int minioPort;

    public MinioConfiguration(@Value("${minio.url}") String url,
                              @Value("${minio.access-key}") String accessKey,
                              @Value("${minio.secret-key}") String secreteKey,
                              @Value("${minio.port}") int minioPort,
                              @Value("${minio.secure}") boolean secure) {
        this.url = url;
        this.accessKey = accessKey;
        this.secreteKey = secreteKey;
        this.minioPort = minioPort;
        this.secure = secure;
    }

    @Bean
    public MinioClient minioClient() {
        MinioClient.Builder builder = MinioClient.builder()
                .credentials(accessKey, secreteKey)
                .endpoint(url, minioPort, secure);
        return builder.build();
    }

    @Bean
    public MinioServiceImpl minioService(MinioClient minioClient,
                                         @Value("${minio.bucket-name}") String bucketName) {
        return new MinioServiceImpl(minioClient, bucketName);
    }
}
