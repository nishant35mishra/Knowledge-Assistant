package com.project.knowledgeassistant.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

	@NotBlank(message = "Question can not be blank")
	private String question;
	
	private String sessionId;
	
}
