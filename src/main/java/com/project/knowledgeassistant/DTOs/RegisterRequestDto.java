package com.project.knowledgeassistant.DTOs;

import com.project.knowledgeassistant.enums.UserRoles;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank
    @Size(min = 4, max = 50 , message = "username must be greater then 4 and less then 50 ")
    private String username;

    @NotBlank
    @Size(min = 4, max = 50 , message = "password must be greater then 4 and less then 50 ")
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private UserRoles role;
}
