package com.project.knowledgeassistant.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.knowledgeassistant.DTOs.ChatHistoryResponseDto;
import com.project.knowledgeassistant.DTOs.ChatRequest;
import com.project.knowledgeassistant.DTOs.ChatResponse;
import com.project.knowledgeassistant.DTOs.ChatSession;
import com.project.knowledgeassistant.DTOs.RagResponseGemini;
import com.project.knowledgeassistant.DTOs.SourceReference;
import com.project.knowledgeassistant.DTOs.SourceResponse;
import com.project.knowledgeassistant.services.ChatHistoryService;
import com.project.knowledgeassistant.services.RagService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@RestController
@RequestMapping("/api/chat")
public class ChatHistoryController {

	private final ChatHistoryService chatServ;
	
	private final RagService ragServ;
	
	// ask a question with session and if session is not present then a new one is generated
	
	@PostMapping("/ask")
	public ResponseEntity<ChatResponse> askQuestion(@Valid @RequestBody ChatRequest request, Authentication auth){
		
		String userEmail = auth.getName();
		String role = auth.getAuthorities().iterator().next().getAuthority();
		
		String sessionId = (request.getSessionId() != null && !request.getSessionId().isBlank())
                ? request.getSessionId().trim()
                : UUID.randomUUID().toString();
		
		//Call RAG engine with question and role-filtered document access
		RagResponseGemini ragResult = ragServ.ask(request.getQuestion(), role);
		
		List<SourceReference> sources = new ArrayList<>();
		if (ragResult.source() != null) {
            for (SourceResponse s : ragResult.source()) {
                sources.add(new SourceReference(s.getDocumentId(),
                        s.getFileName(),
                        
                        "Chunk " + s.getChunkNumber()
                ));
            }
        }
		chatServ.saveChatHistory(sessionId, userEmail, request.getQuestion(), ragResult.answer(), sources);
        
		// Return response to user
        return ResponseEntity.ok(new ChatResponse(ragResult.answer(), sessionId, sources));
		

	}
	
	//only user and admin should be able to do this
	
	@GetMapping("history/{sessionId}")
	public ResponseEntity<List<ChatHistoryResponseDto>> getSessionHistory(@PathVariable String sessionId,Authentication auth){
		String userEmail = auth.getName();
		return ResponseEntity.ok(chatServ.getSessionHistory(sessionId, userEmail));
	}
	
	@GetMapping("/session")
	public ResponseEntity<List<ChatSession>> getUserSession(Authentication auth){
		return ResponseEntity.ok(chatServ.getUserSession(auth.getName()));
	}
	
	//only user and admin should be able to do this
	@DeleteMapping("/delete/{sessionId}")
	public ResponseEntity<com.project.knowledgeassistant.DTOs.ApiResponse<String>> deleteSession(@PathVariable String sessionId, Authentication auth){
		
//		String user = auth.getName();
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
		chatServ.deleteSession(sessionId, auth.getName(),isAdmin);
		return ResponseEntity.ok(com.project.knowledgeassistant.DTOs.ApiResponse.success("Session deleted successfully", sessionId));
	}
}
