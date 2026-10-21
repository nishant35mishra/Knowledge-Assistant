package com.project.knowledgeassistant.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.knowledgeassistant.DTOs.ChatHistoryResponseDto;
import com.project.knowledgeassistant.DTOs.ChatSession;
import com.project.knowledgeassistant.DTOs.SourceReference;
import com.project.knowledgeassistant.configuration.SpringSecurityConfiguration;
import com.project.knowledgeassistant.customexception.NotFound;
import com.project.knowledgeassistant.entities.ChatHistory;
import com.project.knowledgeassistant.enums.SystemCode;
import com.project.knowledgeassistant.repositories.ChatHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
@Data
public class ChatServiceHistory {

    private final SpringSecurityConfiguration springSecurityConfiguration;

	private final ChatHistoryRepository chatRepo;
	
	private final ObjectMapper objMapper;

   
	
	public ChatHistory saveChatHistory(String sessionId, String email, String question, String answer, List<SourceReference> sources ){
		
		String sourcesJson = null;
		if(sources != null && !sources.isEmpty()) {
			try {
				sourcesJson = objMapper.writeValueAsString(sources);
			} catch (JsonProcessingException e) {
			 log.error("Failed to serialize sources to json for a session : {}",sessionId,e	);
			}
		}
		
		ChatHistory chat = new ChatHistory();
		chat.setAnswer(answer);
		chat.setSessionId(sessionId);
		chat.setSourcesJson(sourcesJson);
		chat.setUserEmail(email);
		chat.setUserQuestion(question);
		return chatRepo.save(chat);
	}
	
	
	
	// fetch chronological chat history of evry user
	
	public List<ChatHistoryResponseDto> getSessionHistory(String sessionId, String userEmail){
		List<ChatHistory> chatHistory = chatRepo.findBySessionIdAndUserEmail(sessionId, userEmail);
		if(chatHistory.isEmpty()) {
			throw new NotFound(SystemCode.RAG_CHAT_SESSION_NOT_FOUND, "No chat history found for the session id : "+sessionId);
		}
		return chatHistory.stream().map(this::mapToResponseDto).collect(Collectors.toList());
	}
	
	// get all unique convo of a sesssion of a user
	
	public List<ChatSession> getUserSession(String userEmail){
		List<ChatHistory> userHistory = chatRepo.findByUserEmailOrderByTimestampDesc(userEmail);
		if(userHistory.isEmpty()) {
			return Collections.emptyList();
		}
		Map<String, List<ChatHistory>> grouped = userHistory.stream().collect(Collectors.groupingBy(ChatHistory::getSessionId,LinkedHashMap::new,Collectors.toList()));
		
		// for getting the chat history in sorted manner
		List<ChatSession> sessions = new ArrayList<>();
		for(Entry<String, List<ChatHistory>> map : grouped.entrySet()) {
			String sessionId = map.getKey();
			List<ChatHistory> list = map.getValue();
			
			list.sort(Comparator.comparing(ChatHistory::getTimestamp));
			ChatHistory firstMsg = list.get(0);
			ChatHistory lastMsg = list.get(list.size()-1);
			
			ChatSession chatSession = ChatSession.builder().sesssionId(sessionId).firstQuestion(firstMsg.getUserQuestion()).lastQuestion(lastMsg.getUserQuestion()).lastActive(lastMsg.getTimestamp())
					.messageCount(list.size()).build();
			
			sessions.add(chatSession);
		}
		// for ordering so that most recent chats comes first
		sessions.sort((s1,s2)->s2.getLastActive().compareTo(s1.getLastActive()));
		return sessions;
	}
	
	// delete a session belonging to a user
	public void deleteSession(String sessionId,String userEmail) {
		if(!chatRepo.existsBySessionIdAndUserEmail(sessionId, userEmail)) {
			throw new NotFound(SystemCode.RAG_CHAT_SESSION_NOT_FOUND, "Session not found or access denied for sessionId : "+sessionId);
		}
		chatRepo.deleteBySessionIdAndUserEmail(sessionId, userEmail);
	}
	
	//delete all histroy for a use
	
	public void clearAllHistory(String userEmail) {
		chatRepo.deleteByUserEmail(userEmail);
	}
	
	private ChatHistoryResponseDto mapToResponseDto(ChatHistory history) {
		List<SourceReference> source = Collections.emptyList();
		if(history.getSourcesJson() != null && !history.getSourcesJson().isBlank()) {
			try {
				source = objMapper.readValue(history.getSourcesJson(), new TypeReference<List<SourceReference>>() {});
			} catch (JsonProcessingException e) {
				log.error("Failed to deserialize sourcesJson for chat history id: {}",history.getId(),e);
			}
		}
		return ChatHistoryResponseDto.builder().id(history.getId()).sessionId(history.getSessionId()).userEmail(history.getUserEmail()).userQuestion(history.getUserQuestion()).answer(history.getAnswer()).sources(source).timestamp(history.getTimestamp())
				.build();
	}
}
