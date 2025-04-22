package com.group5.best3deals.unit.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.group5.best3deals.file.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;

    @InjectMocks
    private FileService fileService;

    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(
                "test-bucket",
                "images/testfile.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
    }

    @Test
    void testUploadFile() throws IOException {
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/images/some-uuid_testfile.jpg";

        // Mock the S3 client behavior
        when(amazonS3Client.putObject(any(), any(), any(), any()))
                .thenReturn(new PutObjectResult());
        when(amazonS3Client.getUrl(any(), anyString()))
                .thenReturn(new URL(expectedUrl));

        // Act
        String uploadedUrl = fileService.upload(file);

        // Assert
        assertNotNull(uploadedUrl);

        // Verify that the S3 client methods were called correctly
        verify(amazonS3Client, times(1)).putObject(any(), any(), any(), any());
        verify(amazonS3Client, times(1)).getUrl(any(), anyString());
    }
}