package com.project.knowledgeassistant.controller;


import com.project.knowledgeassistant.DTOs.RagResponse;
import com.project.knowledgeassistant.DTOs.RagResponseGemini;
import com.project.knowledgeassistant.DTOs.SearchRequest;
import com.project.knowledgeassistant.services.GeminiService;
import com.project.knowledgeassistant.services.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
