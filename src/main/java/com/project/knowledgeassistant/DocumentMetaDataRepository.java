package com.project.knowledgeassistant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMetaDataRepository extends JpaRepository<DocumentMetaData, String> {

	
}
