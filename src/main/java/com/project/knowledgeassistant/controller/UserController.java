package com.project.knowledgeassistant.controller;

import com.project.knowledgeassistant.DTOs.LoginDto;
import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/add")
    public ResponseEntity<RegisterResponseDto> AddNewUser(@Valid @RequestBody RegisterRequestDto dto){

        return new ResponseEntity<>(userServices.addUser(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> checkuser(@Valid @RequestBody LoginDto dto){

        return  new ResponseEntity<>(userServices.getUser(dto), HttpStatus.OK);
    }


}
