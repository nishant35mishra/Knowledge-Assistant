package com.project.knowledgeassistant;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@RestController
@RequestMapping("/api/documents")
@Data

public class DocumentController {
 private final DocumentMetaDataServiceImpl dms;
 
 @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<Dto> upload(
         @RequestParam("file") MultipartFile file,
         @RequestParam(value = "accessLevel", defaultValue = "EMPLOYEE") AccessLevel accessLevel) {

     Dto response = dms.uploadDocuments(file, accessLevel);
     return ResponseEntity.status(HttpStatus.CREATED).body(response);
 }
 
 @GetMapping
 public ResponseEntity<List<Dto>> getAll(){
	 return ResponseEntity.ok(dms.getAllDocuments());
 }
 
 @GetMapping("/{id}")
 public ResponseEntity<Dto> getById(@PathVariable String id){
	 return ResponseEntity.ok(dms.getDocumentById(id));
	 
 }
 
 @DeleteMapping("/{id}")
 public ResponseEntity<Dto> deleteDocument(@PathVariable String id){
	 dms.delete(id);
	 return ResponseEntity.noContent().build();
 }
}
