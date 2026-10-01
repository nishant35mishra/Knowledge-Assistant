package com.project.knowledgeassistant.entities;

import com.project.knowledgeassistant.enums.UserRoles;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    private LocalDateTime createdAt =  LocalDateTime.now();
}
