package com.project.knowledgeassistant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.project.knowledgeassistant.entities.ChatHistory;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, String> {

	public List<ChatHistory> findBySessionIdOrderByTimestampAsc(String sessionId);
	
	public List<ChatHistory> findByUserEmailOrderByTimestampDesc(String userEmail);
	
	List<ChatHistory> findBySessionIdAndUserEmail(String sessionId, String userEmail);
	
	boolean existsBySessionIdAndUserEmail(String sessionId, String userEmail);
	
	
	@Modifying
	@Transactional
	public void deleteBySessionIdAndUserEmail(String sessionId, String userEmail); // /deltea particular session of a user
	
	@Modifying
	@Transactional
	public void deleteByUserEmail(String userEmail); // delete all history for a use
}