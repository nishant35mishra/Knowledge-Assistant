package com.project.knowledgeassistant.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {

	@NotBlank(message = "refresh token can not be blank")
	private String refreshToken;
}
