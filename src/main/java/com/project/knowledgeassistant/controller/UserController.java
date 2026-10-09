package com.project.knowledgeassistant.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.knowledgeassistant.DTOs.AuthResponseDto;
import com.project.knowledgeassistant.DTOs.LoginDto;
import com.project.knowledgeassistant.DTOs.RefreshTokenRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterRequestDto;
import com.project.knowledgeassistant.DTOs.RegisterResponseDto;
import com.project.knowledgeassistant.services.UserServices;

import jakarta.validation.Valid;

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
    public ResponseEntity<AuthResponseDto> checkuser(@Valid @RequestBody LoginDto dto){

        return  new ResponseEntity<>(userServices.getUser(dto), HttpStatus.OK);
    }

    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto dto) {
        return ResponseEntity.ok(userServices.refreshAccessToken(dto));}

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveUser(@PathVariable int id, java.security.Principal principal) {
        // principal.getName() automatically retrieves the email of the person who logged in (from the JWT token)
        String message = userServices.approveUser(id, principal.getName());
        return ResponseEntity.ok(message);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<List<RegisterResponseDto>> getPendinUsers(Principal principal){
    	return ResponseEntity.ok(userServices.getPendingUser(principal.getName()));
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectUser(@PathVariable int id, Principal principal) {
        String message = userServices.rejectUser(id, principal.getName());
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestBody @Valid RefreshTokenRequestDto dto){
    	userServices.logOut(dto);
    	return ResponseEntity.ok("You have been logged out");
    	
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RegisterResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userServices.getAllUsers());
    }
}
