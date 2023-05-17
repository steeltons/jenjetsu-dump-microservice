package org.jenjetsu.com.core.service;

import io.minio.errors.MinioException;
import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface S3Service {

    /**
     * <h2>Put object</h2>
     * Method that send file to s3 object storage
     * @param objectName
     * @param objectInputStream
     * @return new UUID name in s3 storage with original file extension
     * @throws MinioException
     */
    public String putObject(String objectName, InputStream objectInputStream) throws MinioException;

    /**
     * <h2>Get object</h2>
     * Method that get object from s3 storage
     * @param objectName
     * @return object resource
     * @throws MinioException
     */
    public Resource getObject(String objectName) throws MinioException;

    /**
     * <h2>Remove object</h2>
     * Method that delete object from s3 storage
     * @param objectName
     * @throws MinioException
     */
    public void removeObject(String objectName) throws MinioException;

}
