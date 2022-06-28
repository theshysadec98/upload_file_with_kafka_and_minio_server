package com.example.uploadfile.service.impl;

import com.example.uploadfile.model.UploadCommand;
import com.example.uploadfile.service.ImageDownloaderService;
import com.example.uploadfile.service.KafkaConsumerService;
import com.example.uploadfile.service.MinioUploaderService;
import com.example.uploadfile.service.ThumbnailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {
    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private ImageDownloaderService imageDownloaderService;

    @Autowired
    private MinioUploaderService minioUploaderService;

    @Autowired
    private ThumbnailService thumbnailService;

    @KafkaListener(topics = "${kafka.uploadcommand.topic}")
    @Override
    public void listen(String message) {
        File imageFile = null;
        File thumbnailFile = null;
        try {
            log.debug("Message arrived {}", message);
            UploadCommand command = jsonMapper.readValue(message, UploadCommand.class);
            log.info("Upload image {} from {}", command.getFileName(), command.getFileName());
            imageFile = imageDownloaderService.downloadImage(command.getSourceUrl(),command.getFileName());
            log.info("Image file created at {}", imageFile.getAbsolutePath());
            minioUploaderService.uploadImage(command.getFileName(), command.getBucket());
            thumbnailFile = thumbnailService.downScaleImage(command.getFileName(), command.getThumbnail());
            log.info("Thumbnail file created at {}", thumbnailFile.getAbsolutePath());
            minioUploaderService.uploadImage(command.getThumbnail(), command.getBucket());
        } catch (XmlParserException |
                 IOException |
                 ServerException |
                 NoSuchAlgorithmException |
                 InsufficientDataException |
                 InvalidKeyException |
                 InvalidResponseException |
                 ErrorResponseException |
                 InternalException e) {
            e.printStackTrace();
        } finally {
            if(imageFile != null) {
                var deleted = imageFile.delete();
                if(deleted){
                    log.info("Image file {} deleted", imageFile.getAbsolutePath());
                }
            }
            if(thumbnailFile != null) {
                var deleted = thumbnailFile.delete();
                if(deleted){
                    log.info("Thumbnail file {} deleted", thumbnailFile.getAbsolutePath());
                }
            }
        }

    }
}
