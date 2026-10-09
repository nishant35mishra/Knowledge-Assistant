package com.project.knowledgeassistant;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.knowledgeassistant.entities.User;
import com.project.knowledgeassistant.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DocumentMetaDataServiceImpl implements DocumentMetaDataService {

	private final DocumentMetaDataRepository repository;
	private final Path uploadDir;
	private final UserRepository us;
	
	public DocumentMetaDataServiceImpl(
            DocumentMetaDataRepository repository,
            @Value("${app.upload.dir:uploads}") String uploadDirPath, UserRepository us) {

        this.repository = repository;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadDir);   // create folder if it doesn't exist
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + this.uploadDir, e);
        }
		this.us = us;
    }
	
	@Override
	public Dto uploadDocuments(MultipartFile file, AccessLevel accesslevel) {
		
		if(file.isEmpty()) {
			throw new IllegalArgumentException("File is Empty");
		}
		
		String originalFileName = file.getOriginalFilename();
		if(originalFileName == null || originalFileName.isBlank()  ) {
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
		else {
			throw new IllegalArgumentException("Unsupported file type");
		}
		
		String uniqueFileName = UUID.randomUUID()+"_"+originalFileName;
		Path targetPath = uploadDir.resolve(uniqueFileName);
		
		try {
			Files.copy(file.getInputStream(),targetPath,StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e){
			throw new RuntimeException("Failed to store file: " + originalFileName, e);
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
		if(accesslevel == AccessLevel.ADMIN && !isAdmin) {
			throw new AccessDeniedException("only ADMIN users can upload documents with ADMIN access level");
		}
		
		//more rules like above can be added for manager also
		
		
		String currentUserName = "System";
		if(auth !=null && auth.isAuthenticated()) {;
		String email = auth.getName();
		Optional<User> userName = us.findByEmail(email);
		if(userName.isPresent())
		currentUserName = userName.get().getUsername();
		
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
		dmd.setUploadBy(currentUserName);
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
                m.getUploadDate(),
                m.getUploadBy()
        );
    }

	@Override
	public List<Dto> getAllDocuments() {
		 
		Set<AccessLevel> allowed = getAllowedAccessLevels();
		return repository.findByAccessLevelIn(allowed).stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public Dto getDocumentById(String id){
       DocumentMetaData dmd = repository.findById(id).orElseThrow(()-> new RuntimeException("File with id : "+id+"does not exist"));
       Set<AccessLevel> allowed = getAllowedAccessLevels();
       if(!allowed.contains(dmd.getAccessLevel())) {
    	   throw new AccessDeniedException("Access Denied : You do not have permission to access this document");
       }
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

	
	private Set<AccessLevel> getAllowedAccessLevels() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null || !auth.isAuthenticated()) {
			return Set.of(AccessLevel.PUBLIC);
		}
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		boolean isAdmin = authorities.stream().anyMatch(a-> a.getAuthority().equals("ROLE_ADMIN"));
		if(isAdmin) {
			return Set.of(AccessLevel.ADMIN,AccessLevel.EMPLOYEE,AccessLevel.MANAGER,AccessLevel.PUBLIC);
		}
		
		boolean isManager = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
		if(isManager) {
			return Set.of(AccessLevel.MANAGER,AccessLevel.EMPLOYEE,AccessLevel.PUBLIC);
		}
		
		boolean isEmployee = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
		if(isEmployee) {
			return Set.of(AccessLevel.PUBLIC,AccessLevel.EMPLOYEE);
		}
		return Set.of(AccessLevel.PUBLIC);
	}
	
	

}
