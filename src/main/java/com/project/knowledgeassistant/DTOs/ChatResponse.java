package com.project.knowledgeassistant.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

	private String answer;
	
	private String sessionId;
	
	//List of cited sources like document,pages,section
	private List<SourceReference> sources;
}
