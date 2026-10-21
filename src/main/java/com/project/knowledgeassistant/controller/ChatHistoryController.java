package com.project.knowledgeassistant.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.knowledgeassistant.DTOs.ChatHistoryResponseDto;
import com.project.knowledgeassistant.DTOs.ChatSession;
import com.project.knowledgeassistant.services.ChatServiceHistory;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

	private final ChatServiceHistory chatServ;
	
	@GetMapping("history/{sessionId}")
	public ResponseEntity<List<ChatHistoryResponseDto>> getSessionHistory(@PathVariable String sessionId,Authentication auth){
		String userEmail = auth.getName();
		return ResponseEntity.ok(chatServ.getSessionHistory(sessionId, userEmail));
	}
	
	@GetMapping("/session")
	public ResponseEntity<List<ChatSession>> getUserSession(Authentication auth){
		return ResponseEntity.ok(chatServ.getUserSession(auth.getName()));
	}
}
