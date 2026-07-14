package com.example.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IFileUploadService {
    String uploadAvatar(MultipartFile file) throws IOException;
}