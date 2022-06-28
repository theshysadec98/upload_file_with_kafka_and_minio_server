package com.example.uploadfile.service.impl;


import com.example.uploadfile.service.MinioUploaderService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class MinioUploaderServiceImpl implements MinioUploaderService {

    @Autowired
    MinioClient minioClient;

    @Value("${bucket.ttl.days}")
    Integer daysToExpire;

    @Override
    public void uploadImage(String source, String bucket) throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        log.info("Uploading image from {} to bucket {}", source, bucket);
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            log.info("Missing bucket {}. Let's try to create it.", bucket);
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build());
            minioClient.setBucketLifecycle(SetBucketLifecycleArgs.builder().bucket(bucket).config(createBucketLifecycle(bucket)).build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(createDownloadPolicy(bucket)).build());
            log.info("Bucket {} was created successfully", bucket);
        }
        log.info("Upload object {}", source);
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucket)
                        .object(source)
                        .filename(source)
                        .build());
    }

    private String createDownloadPolicy(String bucketName) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");
        builder.append("                \"s3:GetBucketLocation\",\n");
        builder.append("                \"s3:ListBucket\"\n");
        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::" + bucketName + "\"\n");
        builder.append("        },\n");
        builder.append("        {\n");
        builder.append("            \"Action\": \"s3:GetObject\",\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n");
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2022-06-26\"\n");
        builder.append("}\n");
        return builder.toString();
    }

    private LifecycleConfiguration createBucketLifecycle(String bucketName) {
        List<LifecycleRule> rules = new LinkedList<>();
        rules.add(new LifecycleRule(
                Status.ENABLED,
                null,
                new Expiration((ZonedDateTime) null, daysToExpire, null),
                new RuleFilter("*"),
                "expirationRule",
                null,
                null,
                null));
        return new LifecycleConfiguration(rules);
    }
}
