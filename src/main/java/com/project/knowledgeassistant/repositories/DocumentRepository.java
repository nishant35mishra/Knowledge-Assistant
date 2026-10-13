package com.project.knowledgeassistant.repositories;


import com.project.knowledgeassistant.entities.Document;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository
        extends JpaRepository<Document, String> {

    List<Document> findAllByAcessLevel(DocumentAcessLevel accessLevel);
    List<Document> findAllByAcessLevelNot(DocumentAcessLevel accessLevel);
}
