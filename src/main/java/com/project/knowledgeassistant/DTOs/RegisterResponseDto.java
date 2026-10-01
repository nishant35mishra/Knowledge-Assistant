package com.project.knowledgeassistant.DTOs;

import com.project.knowledgeassistant.enums.UserRoles;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterResponseDto {

    private int id;
    private String username;
    private String email;
    private UserRoles role;
    private LocalDateTime createdAt ;
}
