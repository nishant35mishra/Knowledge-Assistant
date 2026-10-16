package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.DTOs.RetrievedChunk;
import com.project.knowledgeassistant.enums.DocumentAcessLevel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RetrievalService {

    private final VectorStore vectorStore;

    public RetrievalService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<RetrievedChunk> retrieveRelevantChunks(
            String question,
            DocumentAcessLevel userRole,
            int topK) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Question must not be empty");
        }

        if (userRole == null) {
            throw new IllegalArgumentException(
                    "User role must not be null");
        }

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero");
        }

        // Get the document access levels allowed for this user.
        List<String> allowedLevels = userRole
                .getAccessibleLevels()
                .stream()
                .map(Enum::name)
                .toList();



        FilterExpressionBuilder builder =
                new FilterExpressionBuilder();

        Filter.Expression accessFilter = switch (userRole) {
            case PUBLIC ->
                    builder.eq("documentAcessLevel", "PUBLIC").build();

            case EMPLOYEE ->
                    builder.or(
                            builder.eq("documentAcessLevel", "PUBLIC"),
                            builder.eq("documentAcessLevel", "EMPLOYEE")
                    ).build();

            case MANAGER ->
                    builder.or(
                            builder.eq("documentAcessLevel", "PUBLIC"),
                            builder.or(
                                    builder.eq("documentAcessLevel", "EMPLOYEE"),
                                    builder.eq("documentAcessLevel", "MANAGER")
                            )
                    ).build();

            case ADMIN ->
                    builder.or(
                            builder.eq("documentAcessLevel", "PUBLIC"),
                            builder.or(
                                    builder.eq("documentAcessLevel", "EMPLOYEE"),
                                    builder.or(
                                            builder.eq("documentAcessLevel", "MANAGER"),
                                            builder.eq("documentAcessLevel", "ADMIN")
                                    )
                            )
                    ).build();
        };

        // Perform semantic search with the access filter.
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .filterExpression(accessFilter)
                .build();

        return vectorStore.similaritySearch(searchRequest).stream()
                .map(this::convertToRetrievedChunk)
                .toList();
    }

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
                (Integer) metadata.get("chunkNumber")
        );
    }
}