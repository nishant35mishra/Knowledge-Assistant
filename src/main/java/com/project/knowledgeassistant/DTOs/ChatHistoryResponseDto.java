package com.project.knowledgeassistant.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChatHistoryResponseDto {

	private String id;
	
	private String sessionId;
	
	private String userEmail;
	
	private String userQuestion;
	
	private String answer;

	private List<SourceReference> sources;
	
	private LocalDateTime timestamp ;
	
}
