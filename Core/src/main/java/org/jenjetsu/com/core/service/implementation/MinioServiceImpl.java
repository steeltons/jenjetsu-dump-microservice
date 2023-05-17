package org.jenjetsu.com.core.service.implementation;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.jenjetsu.com.core.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
public class MinioServiceImpl implements S3Service {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioServiceImpl(MinioClient minioClient,
                            @Value("minio.bucket-name") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public String putObject(String objectName, InputStream objectInputStream) throws MinioException{
        try {
            String fileExtension = (objectName.contains(".") ? "." + objectName.split("\\.")[1] : "");
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFilename)
                    .stream(objectInputStream, -1, 10485760)
                    .build();
            minioClient.putObject(putObjectArgs);
            return newFilename;
        } catch (Exception e) {
            log.error("ERROR PUTTING OBJECT IN MINIO SERVICE. Error message: {}", e.getMessage());
            throw new MinioException(e.getMessage());
        } finally {
            if(objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    log.error("ERROR CLOSING INPUT STREAM IN MINIO SERVICE. Error message: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public Resource getObject(String objectName) throws MinioException{
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            InputStream stream = minioClient.getObject(getObjectArgs);
            return new ByteArrayResource(stream.readAllBytes()) {
                public String getFilename() {
                    return objectName;
                }
            };
        } catch (Exception e) {
            log.error("ERROR GETTING OBJECT IN MINIO SERVICE. Error message: {}", e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }

    @Override
    public void removeObject(String objectName) throws MinioException{
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("ERROR DELETING OBJECT IN MINIO SERVICE. Error message: {}", e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }
}
