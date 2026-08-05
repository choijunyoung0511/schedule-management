package com.choijunyoung.schedulemanagement.service.impl;

import com.choijunyoung.schedulemanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path uploadPath;

    public FileStorageServiceImpl(
            @Value("${file.upload-dir}") String uploadDir
    ) {
        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "이미지 저장 폴더를 생성할 수 없습니다.",
                    e
            );
        }
    }

    @Override
    public String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "이미지 파일을 선택해 주세요."
            );
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "JPG, PNG, WEBP 파일만 업로드할 수 있습니다."
            );
        }

        String originalFilename = file.getOriginalFilename();

        String extension = getExtension(originalFilename);

        String savedFilename =
                UUID.randomUUID() + extension;

        Path targetPath =
                uploadPath.resolve(savedFilename).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new IllegalArgumentException(
                    "올바르지 않은 파일 경로입니다."
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "이미지 저장에 실패했습니다.",
                    e
            );
        }

        return "/uploads/" + savedFilename;
    }

    private String getExtension(String filename) {

        if (filename == null ||
                !filename.contains(".")) {
            return "";
        }

        return filename.substring(
                filename.lastIndexOf(".")
        ).toLowerCase();
    }
}