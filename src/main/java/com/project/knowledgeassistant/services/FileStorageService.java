package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.DTOs.OriginalDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation;

    public FileStorageService(
            @Value("${app.file-storage.path}") String storagePath) {

        this.storageLocation =
                Paths.get(storagePath)
                        .toAbsolutePath()
                        .normalize();

        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create file storage directory",
                    e
            );
        }
    }



    public OriginalDocument store(MultipartFile file) {

        try {

            String originalFileName =
                    file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {

                throw new IllegalArgumentException(
                        "Invalid file name"
                );
            }

            String storedFileName =
                    UUID.randomUUID()
                            + "_"
                            + Paths.get(originalFileName)
                            .getFileName();

            Path targetLocation =
                    storageLocation.resolve(storedFileName);

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            OriginalDocument originalDocument = new  OriginalDocument();
            originalDocument.setProcessedFileName(storedFileName);
            originalDocument.setProcessedFilePath(targetLocation);
            originalDocument.setProcessedFileType(file.getContentType());


            return originalDocument;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to store file",
                    e
            );
        }
    }

    public void delete(String filePath) {

        try {
            Files.deleteIfExists(
                    Paths.get(filePath)
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to delete file",
                    e
            );
        }
    }
}
