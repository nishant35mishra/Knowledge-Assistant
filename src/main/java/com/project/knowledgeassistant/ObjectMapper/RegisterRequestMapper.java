package com.project.knowledgeassistant.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.entities.MyUser;
import org.springframework.stereotype.Component;

@Component
public class RegisterRequestMapper {

    public MyUser RegisterRequestMapper(RegisterRequestDto dto) {

        MyUser myUser = new MyUser();
        myUser.setUsername(dto.getUsername());
        myUser.setPassword(dto.getPassword());
        myUser.setEmail(dto.getEmail());
        myUser.setRole(dto.getRole());

        return myUser;

    }

    public RegisterResponseDto  RegisterResponseMapper(MyUser user) {

        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setId(user.getId());
        registerResponseDto.setUsername(user.getUsername());
        registerResponseDto.setEmail(user.getEmail());
        registerResponseDto.setRole(user.getRole());
        registerResponseDto.setCreatedAt(user.getCreatedAt());



        return   registerResponseDto;
    }

}
