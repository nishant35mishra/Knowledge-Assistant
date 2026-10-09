package com.project.knowledgeassistant.DTOs;

import java.time.LocalDateTime;
import java.util.Set;

import com.project.knowledgeassistant.enums.AccountStatus;

import lombok.Data;

@Data
public class RegisterResponseDto {

    private int id;
    private String username;
    private String email;
    private Set<String> roles;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt ;
}
