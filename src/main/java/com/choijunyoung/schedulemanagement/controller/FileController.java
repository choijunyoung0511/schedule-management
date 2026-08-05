package com.choijunyoung.schedulemanagement.controller;

import com.choijunyoung.schedulemanagement.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(
            FileStorageService fileStorageService
    ) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/images")
    public Map<String, String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        String imageUrl =
                fileStorageService.saveImage(file);

        return Map.of(
                "imageUrl",
                imageUrl
        );
    }
}