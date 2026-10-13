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
import java.util.stream.Collectors;

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

    public DocumentResponse getDocumentById(String id , String email) {

        Document document = documentRepository.findById(id).orElseThrow(()-> new NotFound("Document not found"));

        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFound("User not found"));

        Set<Role> roles = user.getRoles();
        String userRole = "" ;
        DocumentAcessLevel acessLevel = document.getAcessLevel();

        for(Role role : roles){

            if(role.getRole().equals("ROLE_ADMIN")){
                userRole = "ADMIN";
            }else if(role.getRole().equals("ROLE_EMPLOYEE")){
                userRole = "EMPLOYEE";
            } else if (role.getRole().equals("ROLE_MANAGER")) {
                userRole = "MANAGER";
            }else if(role.getRole().equals("ROLE_PUBLIC")){
                userRole = "PUBLIC";
            }

        }

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

    public List<DocumentResponse> getAllDocuments(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFound("User not found"));

        Set<Role> roles = user.getRoles();
        DocumentAcessLevel userRole = DocumentAcessLevel.PUBLIC;



        for(Role role : roles){

            if(role.getRole().equals("ROLE_ADMIN")){
                userRole = DocumentAcessLevel.ADMIN;
            }else if(role.getRole().equals("ROLE_EMPLOYEE")){
                userRole = DocumentAcessLevel.EMPLOYEE;
            } else if (role.getRole().equals("ROLE_MANAGER")) {
                userRole =  DocumentAcessLevel.MANAGER ;
            }else if(role.getRole().equals("ROLE_PUBLIC")){
                userRole =    DocumentAcessLevel.PUBLIC;
            }

        }

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
    public Void  deleteDocumentById(String id , String email) {

        Document document = documentRepository.findById(id).orElseThrow(()-> new NotFound("Document not found"));

        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFound("User not found"));

//        List<org.springframework.ai.document.Document> documents = vectorStoreService.findByDocumentId(document.getId());

        Set<Role> roles = user.getRoles();
        String userRole = "" ;
        DocumentAcessLevel acessLevel = document.getAcessLevel();

        for(Role role : roles){

            if(role.getRole().equals("ROLE_ADMIN")){
               try{
                   deletePhysicalFileAndDocumentChunks(document.getFilePath(),document.getProcessedFilePath(), document.getId());
                   documentRepository.deleteById(id);
               }catch(Exception e){

                   System.out.println(e);
               }
            }else {

                if(document.getAcessLevel().equals(DocumentAcessLevel.ADMIN)){
                    throw new IllegalArgumentException("invalid acess level");
                }else {
                    try{
                        deletePhysicalFileAndDocumentChunks(document.getFilePath(),document.getProcessedFilePath(), document.getId());
                        documentRepository.deleteById(id);
                    }catch(Exception e){

                        System.out.println(e);
                    }
                }
            }
        }


        return null;
    }

    public void deletePhysicalFileAndDocumentChunks(String originalFilePath , String processedFilePath , String documentId) throws IOException {
        Path originalpath = Path.of(originalFilePath);
        Path processedpath = Path.of(processedFilePath);

        Files.deleteIfExists(originalpath);
        Files.deleteIfExists(processedpath);

        vectorStoreService.deleteChunksByDocumentId(documentId);

    }


    public DocumentResponse getDocumentResponse (Document document) {
        DocumentResponse documentResponse = new DocumentResponse();
        documentResponse.setDocumentId(document.getId());
        documentResponse.setFileName(document.getFileName());
        documentResponse.setProcessedFilePath(document.getProcessedFilePath());
        documentResponse.setProcessedFileName(document.getProcessedFileName());

        documentResponse.setTotalChunks(document.getTotalChunks());
        documentResponse.setTotalPages(document.getTotalPages());
        documentResponse.setUploadDate(document.getUploadDate());
        documentResponse.setFileType(document.getFileType());
        documentResponse.setProcessedFileType(document.getProcessedFileType());
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
