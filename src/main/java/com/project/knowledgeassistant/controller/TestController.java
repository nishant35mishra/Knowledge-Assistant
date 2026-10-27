package com.project.knowledgeassistant.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.knowledgeassistant.DTOs.SearchRequest;
import com.project.knowledgeassistant.services.EmbeddingService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TestController {
	
	private final EmbeddingService embed;
	
	@PostMapping("/kuch")
	public float[] askQuestion(@RequestBody SearchRequest req){
		return embed.generateEmbedding(req.getQuestion());
	}

    @GetMapping("/")
    public String test(){
        return "home Page ";
    }

    @GetMapping("/test1")
    @PreAuthorize("hasRole('ADMIN')")
    public String TestController1(){

        return "success" ;

    }

    @GetMapping("/test2")
    @PreAuthorize("hasRole('MANAGER')")
    public String TestController2(){

        return "success" ;

    }

    @GetMapping("/test3")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String TestController3(){

        return "success" ;

    }

    @GetMapping("/test4")
    @PreAuthorize("hasRole('PUBLIC')")
    public String TestController4(){

        return "success" ;

    }

    @GetMapping("/test5")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public String TestController5(){

        return "success" ;

    }
}
