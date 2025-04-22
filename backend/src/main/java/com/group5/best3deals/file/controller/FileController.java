package com.group5.best3deals.file.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
@Tag(name = "File Upload", description = "API for File Upload")
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Store a file to get a link",
            description = "When using this path, you need to prefix a full server url before the link.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        try {
            String url = fileService.upload(file);
            return ResponseEntity.ok(new ApiResponse<>(true, "File upload successful", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "File upload failed: " + e.getMessage()));
        }
    }
}
