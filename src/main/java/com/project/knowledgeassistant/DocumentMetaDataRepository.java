package com.project.knowledgeassistant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface DocumentMetaDataRepository extends JpaRepository<DocumentMetaData, String> {

	List<DocumentMetaData> findByAccessLevelIn(Collection<AccessLevel> accessLevel);
}
