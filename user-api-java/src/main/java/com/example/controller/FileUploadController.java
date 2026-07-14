package com.example.controller;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.service.IFileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final IFileUploadService fileUploadService;

    public FileUploadController(IFileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/avatar")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileUploadService.uploadAvatar(file);
        return Map.of("url", url);
    }

    @GetMapping("/avatar/{filename:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads/avatars/").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                    contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
        }
        throw new RuntimeException("File not found");
    }
}