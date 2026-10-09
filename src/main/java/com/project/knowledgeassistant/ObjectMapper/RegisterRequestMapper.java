package com.project.knowledgeassistant.ObjectMapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.entities.Role;
import com.project.knowledgeassistant.entities.User;

@Component
public class RegisterRequestMapper {

    public User RegisterRequestMapper(RegisterRequestDto dto) {

        User myUser = new User();
        myUser.setUsername(dto.getUsername());
        myUser.setPassword(dto.getPassword());
        myUser.setEmail(dto.getEmail());
       // myUser.setRole(dto.getRole());

        return myUser;

    }

    public RegisterResponseDto  RegisterResponseMapper(User user) {

        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setId(user.getId());
        registerResponseDto.setUsername(user.getUsername());
        registerResponseDto.setEmail(user.getEmail());
        registerResponseDto.setAccountStatus(user.getAccountStatus());
        registerResponseDto.setCreatedAt(user.getCreatedAt());


        if (user.getRoles() != null) {
            registerResponseDto.setRoles(
                user.getRoles().stream()
                    .map(Role::getRole)
                    .collect(Collectors.toSet())
            );
        }

        return   registerResponseDto;
    }

}
