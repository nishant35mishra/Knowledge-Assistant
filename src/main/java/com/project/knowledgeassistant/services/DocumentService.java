package com.project.knowledgeassistant.services;






import com.project.knowledgeassistant.CustomException.NotFound;
import com.project.knowledgeassistant.DTOs.*;
import com.project.knowledgeassistant.entities.Document;
import com.project.knowledgeassistant.entities.Role;
import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import com.project.knowledgeassistant.repositories.DocumentRepository;
import com.project.knowledgeassistant.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentService {

    private final FileStorageService fileStorageService;
    private final TextExtractionService textExtractionService;
    private final ChunkingService chunkingService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final VectorStoreService vectorStoreService;



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


            List<DocumentChunkData> chunks = chunkingService.createChunksWithOverlap(extractedText) ;

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

            document.setTotalChunks(chunks.size());

//            document.setUploadBy(userRepository.findByEmail(email).get().getUsername());


            User userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFound("User not found with email: " + email));

            document.setUploadBy(userEntity);

            Set<Role> roles = userEntity.getRoles();
            String userRole = "" ;

            for(Role role : roles){

                if(role.getRole().equals("ROLE_ADMIN")){
                    userRole = "ADMIN";
                }else if(role.getRole().equals("ROLE_EMPLOYEE")){
                    userRole = "EMPLOYEE";
                } else if (role.getRole().equals("ROLE_MANAGER")) {
                    userRole = "MANAGER";
                }

            }

            if(userRole.equals("ADMIN")){
                document.setAcessLevel(acessLevel);
            }
            else if(userRole.equals("EMPLOYEE")){

                boolean  isvalid =   acessLevel.equals(DocumentAcessLevel.EMPLOYEE )||acessLevel.equals(DocumentAcessLevel.PUBLIC) ;
                if(isvalid){
                    document.setAcessLevel(acessLevel);
                }else {

                    throw new IllegalArgumentException("invalid acess level");
                }
            }

            else {
                boolean  isvalid =   !acessLevel.equals(DocumentAcessLevel.ADMIN);

                if (isvalid){
                    document.setAcessLevel(acessLevel);
                }
                else {throw new IllegalArgumentException("Invalid acess level");}
            }


            documentRepository.save(document);

            List<org.springframework.ai.document.Document> vectorDocuments =
                    new ArrayList<>();

            for (DocumentChunkData chunk : chunks) {

                Map<String, Object> metadata = Map.of(
                        "documentId", document.getId(),
                        "fileName", document.getFileName(),
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
                    document.isProcessed(),
                    "Document uploaded and processed successfully"

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

    public DocumentResponse getDocumentById(String id , String role) {

        Document document = documentRepository.findById(id).orElseThrow(()-> new NotFound("Document not found"));

        String userRole = role.substring(5) ;
        DocumentAcessLevel acessLevel = document.getAcessLevel();


        if(userRole.equals("ADMIN")){
            return getDocumentResponse(document);
        }
        else if(userRole.equals("EMPLOYEE")){

            boolean  isvalid =   acessLevel.equals(DocumentAcessLevel.EMPLOYEE )||acessLevel.equals(DocumentAcessLevel.PUBLIC) ;
            if(isvalid){
                return getDocumentResponse(document);
            }else {

                throw new IllegalArgumentException("invalid acess level");
            }
        } else if (userRole.equals("PUBLIC")) {

            boolean  isvalid =   acessLevel.equals(DocumentAcessLevel.PUBLIC);
            if(isvalid){
                return getDocumentResponse(document);
            }else  {
                throw new IllegalArgumentException("invalid acess level");
            }

        } else {
            boolean  isvalid =   !acessLevel.equals(DocumentAcessLevel.ADMIN);

            if (isvalid){
                return getDocumentResponse(document);
            }
            else {throw new IllegalArgumentException("Invalid acess level");}
        }


    }

    public List<DocumentResponse> getAllDocuments(String role) {



        DocumentAcessLevel userRole = DocumentAcessLevel.valueOf(role.substring(5));





        if(userRole.equals(DocumentAcessLevel.PUBLIC)){
            List<Document> documents = documentRepository.findAllByAcessLevel(userRole);
            return documents.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }
        else if (userRole.equals(DocumentAcessLevel.EMPLOYEE)){

            List<Document> documents1 = documentRepository.findAllByAcessLevel(DocumentAcessLevel.PUBLIC);
            List<Document> documents2 = documentRepository.findAllByAcessLevel(DocumentAcessLevel.EMPLOYEE);

            List<Document> allDocuments = new ArrayList<>();
            allDocuments.addAll(documents1);
            allDocuments.addAll(documents2);
            return allDocuments.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }

        else if (userRole.equals(DocumentAcessLevel.MANAGER)){
            List<Document> documents = documentRepository.findAllByAcessLevelNot(DocumentAcessLevel.ADMIN);
            return documents.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }

        else {

            List<Document> documents = documentRepository.findAll();
            return documents.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }




    }

    @Transactional
    public Void  deleteDocumentById(String id , String role) {

        Document document = documentRepository.findById(id).orElseThrow(()-> new NotFound("Document not found"));


            if(role.equals("ROLE_ADMIN")){
                try{
                    deletePhysicalFileAndDocumentChunks(document.getFilePath(), document.getId());
                    documentRepository.deleteById(id);
                }catch(Exception e){

                    System.out.println(e);
                }
            }else {

                if(document.getAcessLevel().equals(DocumentAcessLevel.ADMIN)){
                    throw new IllegalArgumentException("invalid acess level");
                }else {
                    try{
                        deletePhysicalFileAndDocumentChunks(document.getFilePath(), document.getId());
                        documentRepository.deleteById(id);
                    }catch(Exception e){

                        System.out.println(e);
                    }
                }
            }


        return null;
    }

    public void deletePhysicalFileAndDocumentChunks(String originalFilePath  , String documentId) throws IOException {
        Path originalpath = Path.of(originalFilePath);

        Files.deleteIfExists(originalpath);

        vectorStoreService.deleteChunksByDocumentId(documentId);

    }


    public DocumentResponse getDocumentResponse (Document document) {
        DocumentResponse documentResponse = new DocumentResponse();
        documentResponse.setDocumentId(document.getId());
        documentResponse.setFileName(document.getFileName());
        documentResponse.setTotalChunks(document.getTotalChunks());
        documentResponse.setUploadDate(document.getUploadDate());
        documentResponse.setFileType(document.getFileType());
        documentResponse.setProcessed(document.isProcessed());
        documentResponse.setDocumentAcessLevel(document.getAcessLevel());
        documentResponse.setFileSize(document.getFileSize());
        documentResponse.setFilePath(document.getFilePath());

        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setId(document.getUploadBy().getId());
        registerResponseDto.setUsername(document.getUploadBy().getUsername());
        registerResponseDto.setEmail(document.getUploadBy().getEmail());

        Set<String> set = new HashSet<>();
        for(Role role : document.getUploadBy().getRoles()){
            set.add(role.getRole());
        }
        registerResponseDto.setRoles(set);
        registerResponseDto.setCreatedAt(document.getUploadBy().getCreatedAt());
        registerResponseDto.setAccountStatus(document.getUploadBy().getAccountStatus());
        documentResponse.setRegisterResponseDto(registerResponseDto);


        return documentResponse;
    }


}

