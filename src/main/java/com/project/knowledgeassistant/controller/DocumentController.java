package com.project.knowledgeassistant.controller;



import com.project.knowledgeassistant.DTOs.DocumentResponse;
import com.project.knowledgeassistant.DTOs.DocumentUploadResponse;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import com.project.knowledgeassistant.services.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/documents")
@Slf4j


public class DocumentController {

    private final DocumentService documentService;



    @PostMapping("/upload/")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER')")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file , @RequestParam(value = "accessLevel", defaultValue = "PUBLIC") DocumentAcessLevel accessLevel ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        DocumentUploadResponse response =
                documentService.uploadDocument(file,authentication.getName(),accessLevel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/upload2/")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER')")
    public ResponseEntity<DocumentUploadResponse> uploadDocument2(
            @RequestParam("file") MultipartFile file , @RequestParam(value = "accessLevel", defaultValue = "PUBLIC") DocumentAcessLevel accessLevel ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        DocumentUploadResponse response =
                documentService.uploadDocument(file,authentication.getName(),accessLevel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().toString();

        DocumentResponse response = documentService.getDocumentById(id,role);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/get/")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER')")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().toString();

        List<DocumentResponse> response = documentService.getAllDocuments(role);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteDocumentById(@PathVariable String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().toString();

         documentService.deleteDocumentById(id,role);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("document deleted successfully");
    }




}
