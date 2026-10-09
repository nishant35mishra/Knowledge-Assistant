package com.project.knowledgeassistant.services;




import com.project.knowledgeassistant.DTOs.*;
import com.project.knowledgeassistant.entities.Document;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import com.project.knowledgeassistant.repositories.DocumentRepository;
import com.project.knowledgeassistant.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@AllArgsConstructor
public class DocumentService {

    private final FileStorageService fileStorageService;
    private final TextExtractionService textExtractionService;
    private final ChunkingService chunkingService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final DocumentConversionService documentConversionService;



    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword"
    );




    public DocumentUploadResponse uploadDocument(
            MultipartFile file , String email , DocumentAcessLevel acessLevel) {

        validateFile(file);

        String storedFilePath = null;



        try {


            String fileType =
                    textExtractionService.detectFileType(file);


            validateFileType(fileType);

            OriginalDocument originalDocument = fileStorageService.store(file);


            storedFilePath = originalDocument.getProcessedFilePath().toString();

            String fileName = originalDocument.getProcessedFileName() ;





            String extractedText =
                    textExtractionService.extractText(file);

            if (extractedText == null ||
                    extractedText.isBlank()) {

                throw new IllegalArgumentException(
                        "No readable text found in document"
                );
            }

            List<ExtractedPage> pages = new ArrayList<>() ;
            List<DocumentChunkData> chunks = new ArrayList<>();



            Document document = new Document();

            document.setId(
                    UUID.randomUUID().toString()
            );

            document.setFileName(
                    fileName
            );

            document.setFileType(fileType);

            document.setFilePath(
                    storedFilePath
            );

            document.setFileSize(
                    file.getSize()
            );

            document.setProcessed(true);



            document.setAcessLevel(acessLevel);

//            document.setUploadBy(userRepository.findByEmail(email).get().getUsername());
            document.setUploadBy("System");







            if (!fileType.equals("application/pdf") ) {



                 ProcessedDocument processedDocument = documentConversionService.convertToPdf(
                        Paths.get(storedFilePath),
                        Paths.get("C:\\Users\\Aditya\\OneDrive\\Desktop\\Knowledge-Assistant\\uploads\\processed").toAbsolutePath().normalize()
                );



                try (InputStream inputStream = Files.newInputStream(processedDocument.getProcessedFilePath())) {



                     pages =
                            textExtractionService.extractPagesUsingPath(processedDocument.getProcessedFilePath());

                     chunks =
                            chunkingService.createChunks(pages);

                    document.setProcessedFileName(processedDocument.getProcessedFileName());
                    document.setProcessedFilePath(processedDocument.getProcessedFilePath().toString());
                    document.setProcessedFileType(processedDocument.getProcessedFileType());
                    document.setTotalChunks(
                            chunks.size()
                    );
                    document.setTotalPages(pages.size());
                }



            }else {

                Path directory = Path.of("C:\\Users\\Aditya\\OneDrive\\Desktop\\Knowledge-Assistant\\uploads\\processed");

                Files.createDirectories(directory);

                Path newPath = directory.resolve(fileName);

                try {
                    Files.copy(
                            file.getInputStream(),
                            newPath,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    System.out.println("File saved at: " + newPath.toAbsolutePath());

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                document.setProcessedFileName(fileName);
                document.setProcessedFilePath(newPath.toString());
                document.setProcessedFileType(fileType);



                 pages =
                        textExtractionService.extractPages(file.getInputStream());

                 chunks =
                        chunkingService.createChunks(pages);

                document.setTotalChunks(
                        chunks.size()
                );
                document.setTotalPages(pages.size());
            }

            documentRepository.save(document);

            List<org.springframework.ai.document.Document> vectorDocuments =
                    new ArrayList<>();

            for (DocumentChunkData chunk : chunks) {

                Map<String, Object> metadata = Map.of(
                        "documentId", document.getId(),
                        "fileName", document.getFileName(),
                        "pageNumber", chunk.pageNumber(),
                        "chunkNumber", chunk.chunkNumber(),
                        "uploadDate", document.getUploadDate().toString(),
                        "documentType", document.getFileType() ,
                        "documentAcessLevel", document.getAcessLevel()
                );

                org.springframework.ai.document.Document vectorDocument =
                        new org.springframework.ai.document.Document(
                                chunk.content(),
                                metadata
                        );

                vectorDocuments.add(vectorDocument);
            }

            vectorStoreService.saveChunks(vectorDocuments);

            return new DocumentUploadResponse(
                    document.getId(),
                    document.getFileName(),
                    document.getFileType(),
                    document.getFileSize(),
                    document.getTotalChunks(),
                    document.isProcessed(),
                    "Document uploaded and processed successfully" ,
                    document.getUploadBy() ,
                    pages ,
                    chunks ,
                    acessLevel,
                    document.getUploadDate() ,
                    document.getProcessedFileName(),
                    document.getProcessedFileType() ,
                    document.getProcessedFilePath()
            );

        } catch (Exception e) {

            // If DB/Tika/chunking fails after file storage,
            // remove the orphaned original file.
            if (storedFilePath != null) {
                try {
                    fileStorageService.delete(
                            storedFilePath
                    );
                } catch (Exception ignored) {
                    // Log this in production
                }
            }

            throw new RuntimeException(
                    "Document processing failed",
                    e
            );
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File cannot be empty"
            );
        }
    }

    private void validateFileType(String fileType) {

        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType
            );
        }
    }
}
