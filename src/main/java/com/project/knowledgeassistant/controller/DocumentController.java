package com.project.knowledgeassistant.controller;



import com.project.knowledgeassistant.DTOs.DocumentUploadResponse;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import com.project.knowledgeassistant.services.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/documents")
@Slf4j


public class DocumentController {

    private final DocumentService documentService;


    @PostMapping("/upload/two")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file , @RequestParam(value = "accessLevel", defaultValue = "EMPLOYEE") DocumentAcessLevel accessLevel ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        DocumentUploadResponse response =
                documentService.uploadDocument(file,authentication.getName(),accessLevel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
