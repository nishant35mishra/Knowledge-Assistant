package com.project.knowledgeassistant.controller;


import com.project.knowledgeassistant.DTOs.RagResponseGemini;
import com.project.knowledgeassistant.DTOs.SearchRequest;
import com.project.knowledgeassistant.services.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class GeminiController {

    private final RagService ragService;
    private final ChatModel chatModel;

    @PostMapping ("/gemini")
    public String testGemini(@RequestBody SearchRequest searchRequest) {

        return chatModel.call(searchRequest.getQuestion());
    }

    @PostMapping("/ask")
    public RagResponseGemini askGemini(@RequestBody SearchRequest searchRequest) {

        return ragService.ask(searchRequest.getQuestion()) ;

    }

}
