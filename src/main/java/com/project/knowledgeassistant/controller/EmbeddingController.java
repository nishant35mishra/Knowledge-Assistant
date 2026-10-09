package com.project.knowledgeassistant.controller;

import com.project.knowledgeassistant.services.EmbeddingService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping("/test")
    public Map<String, Object> test(@RequestParam String text) {

        float[] embedding =
                embeddingService.generateEmbedding(text);

        return Map.of(
                "text", text,
                "dimensions", embedding.length,
                "embedding", embedding
        );
    }
}
