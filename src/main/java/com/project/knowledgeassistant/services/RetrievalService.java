package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.DTOs.RetrievedChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    public List<RetrievedChunk> retrieve(
            String question,
            int topK
    ) {

        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(question)
                                .topK(topK)
                                .build()
                );

        return documents.stream()
                .map(this::convertToRetrievedChunk)
                .toList();
    }

    private RetrievedChunk convertToRetrievedChunk(
            Document document
    ) {

        Map<String, Object> metadata =
                document.getMetadata();

        return new RetrievedChunk(
                document.getText(),
                (String) metadata.get("documentId"),
                (String) metadata.get("fileName"),
                (Integer) metadata.get("pageNumber"),
                (Integer) metadata.get("chunkNumber"),
                null
        );
    }
}
