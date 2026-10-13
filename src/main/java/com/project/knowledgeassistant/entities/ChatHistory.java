package com.project.knowledgeassistant.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_history")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ChatHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(nullable = false)
	private String sessionId;
	
	@Column(nullable = false)
	private String userEmail;
	
	@Column(columnDefinition = "TEXT",nullable = false)
	private String userQuestion;
	
	@Column(columnDefinition = "TEXT",nullable = false)
	private String answer;
	
	@Column(columnDefinition = "TEXT")
	private String sourcesJson;
	
	@Column(nullable = false)
	private LocalDateTime timestamp = LocalDateTime.now();
}
