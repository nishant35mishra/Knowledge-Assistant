package com.project.knowledgeassistant.controller;

import com.project.knowledgeassistant.DTOs.RetrievedChunk;
import com.project.knowledgeassistant.DTOs.SearchRequest;
import com.project.knowledgeassistant.services.RetrievalService;
import lombok.RequiredArgsConstructor;
//import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RetrievalService retrievalService;

    @PostMapping("/search")
    public List<RetrievedChunk> search(
            @RequestBody SearchRequest request
    ) {

        return retrievalService.retrieve(
                request.getQuestion(),
                5
        );
    }
}
