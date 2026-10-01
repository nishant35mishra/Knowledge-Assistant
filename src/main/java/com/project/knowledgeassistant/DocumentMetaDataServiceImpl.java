package com.project.knowledgeassistant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentMetaDataServiceImpl implements DocumentMetaDataService {

	private final DocumentMetaDataRepository repository;
	private final Path uploadDir;
	
	public DocumentMetaDataServiceImpl(
            DocumentMetaDataRepository repository,
            @Value("${app.upload.dir:uploads}") String uploadDirPath) {

        this.repository = repository;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadDir);   // create folder if it doesn't exist
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + this.uploadDir, e);
        }
    }
	
	@Override
	public Dto uploadDocuments(MultipartFile file, AccessLevel accesslevel) {
		// TODO Auto-generated method stub
		if(file.isEmpty()) {
			throw new IllegalArgumentException("File is Empty");
		}
		
		String originalFileName = file.getOriginalFilename();
		if(originalFileName.isBlank() || originalFileName == null ) {
			throw new IllegalArgumentException("File Name is Missing");
		}
		
		String lowerName = originalFileName.toLowerCase();
		String fileType = null;
		if(lowerName.endsWith(".pdf")) {
			fileType = "PDF";
		}
			
		else if(lowerName.endsWith(".docx")) {	
			fileType = "DOCX";
		}
		
		else if(lowerName.endsWith(".txt")) {
			fileType = "TXT";
		}
		
		String uniqueFileName = UUID.randomUUID()+"_"+originalFileName;
		Path targetPath = uploadDir.resolve(uniqueFileName);
		
		try {
			Files.copy(file.getInputStream(),targetPath,StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e){
			throw new RuntimeException("Failed to store file: " + originalFileName, e);
		}
		
		DocumentMetaData dmd = new DocumentMetaData();
		dmd.setId(UUID.randomUUID().toString());
		dmd.setFileName(uniqueFileName);
		dmd.setFilePath(targetPath.toString());
		dmd.setFileSize(file.getSize());
		dmd.setFileType(fileType);
		dmd.setAccessLevel(accesslevel);
		dmd.setProcessed(false);
		dmd.setTotalChunks(0);
		dmd.setUploadBy("System");
		dmd.setUploadDate(LocalDateTime.now());
		
		DocumentMetaData saved = repository.save(dmd);
		return toResponse(saved);
	}

	private Dto toResponse(DocumentMetaData m) {
        return new Dto(
                m.getId(),
                m.getFileName(),
                m.getFileType(),
                m.getFileSize(),
                m.getAccessLevel(),
                m.isProcessed(),
                m.getTotalChunks(),
                m.getUploadDate()
        );
    }

	@Override
	public List<Dto> getAllDocuments() {
		 
		return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public Dto getDocumentById(String id) {
       DocumentMetaData dmd = repository.findById(id).orElseThrow(()-> new RuntimeException("File with id : "+id+"does not exist"));
       return toResponse(dmd);
	}

	@Override
	public void delete(String id) {
		DocumentMetaData dmd = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
		
		try {
		Path path = Paths.get(dmd.getFilePath());
		Files.deleteIfExists(path);
		}
		catch(IOException ex) {
			System.err.println("Could not delete file from disk: " + dmd.getFilePath());
		}
		
		repository.delete(dmd);
	}

}
