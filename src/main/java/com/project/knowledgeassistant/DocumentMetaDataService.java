package com.project.knowledgeassistant;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentMetaDataService {

	public Dto uploadDocuments(MultipartFile file,AccessLevel accesslevel);
	
	public List<Dto> getAllDocuments();
	
	public Dto getDocumentById(String id);
	
	public void delete(String id);
	

}
