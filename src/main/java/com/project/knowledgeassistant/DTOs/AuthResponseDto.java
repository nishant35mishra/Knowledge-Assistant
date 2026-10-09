package com.project.knowledgeassistant.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor

public class AuthResponseDto {

	private String accessToken;
	
	private String refreshToken;
	
	private String userName;
	
	private String email;
	
	private List<String> roles;
	
}
