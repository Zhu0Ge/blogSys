package com.example.service.impl;

import com.example.service.IFileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements IFileUploadService {

    private final String uploadDir = "uploads/avatars/";

    public String uploadAvatar(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String ext = contentType.substring(contentType.lastIndexOf("/") + 1);
        if (ext.equals("jpeg")) ext = "jpg";
        String fileName = UUID.randomUUID().toString() + "." + ext;

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/api/upload/avatar/" + fileName;
    }
}