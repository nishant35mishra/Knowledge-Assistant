package com.project.knowledgeassistant.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.knowledgeassistant.DTOs.RagResponseGemini;
import com.project.knowledgeassistant.DTOs.SearchRequest;
import com.project.knowledgeassistant.services.GeminiService;
import com.project.knowledgeassistant.services.RagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class GeminiTestController {

    private final GeminiService geminiService;

    private final RagService ragService;

    @GetMapping("/gemini")
    public String testGemini() {

        return geminiService.test();
    }

    @PostMapping("/ask")
    public RagResponseGemini askGemini(@RequestBody SearchRequest searchRequest) {

        return ragService.ask(searchRequest.getQuestion()) ;

    }

}
