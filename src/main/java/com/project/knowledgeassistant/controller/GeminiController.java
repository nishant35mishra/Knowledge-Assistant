package com.project.knowledgeassistant.controller;


import com.project.knowledgeassistant.DTOs.RagResponseGemini;
import com.project.knowledgeassistant.DTOs.SearchRequest;
import com.project.knowledgeassistant.services.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class GeminiController {

    private final RagService ragService;
    private final ChatModel chatModel;

    @PostMapping ("/gemini")
    public String testGemini(@RequestBody SearchRequest searchRequest) {

        return chatModel.call(searchRequest.getQuestion());
    }

    @PostMapping("/ask")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
    public RagResponseGemini askGemini(@RequestBody SearchRequest searchRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().toString();
        log.info("role: {}", role);

        return ragService.ask(searchRequest.getQuestion(),role) ;

    }

}
