package com.project.knowledgeassistant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.knowledgeassistant.entities.ChatHistory;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, String> {

	public List<ChatHistory> findBySessionIdOrderByTimestampAsc(String sessionId);
	
	public List<ChatHistory> findByUserEmailOrderByTimestampDesc(String userEmail);
	
	public void deleteBySessionId(String sessionId);
}