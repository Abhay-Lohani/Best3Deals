package com.group5.best3deals.file.service;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) throws IOException {
        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = "images/" + fileName;

        // Upload the file
        amazonS3Client.putObject(bucket, filePath, file.getInputStream(), null);

        // Return the URL of the uploaded file
        return amazonS3Client.getUrl(bucket, filePath).toString();
    }
}
