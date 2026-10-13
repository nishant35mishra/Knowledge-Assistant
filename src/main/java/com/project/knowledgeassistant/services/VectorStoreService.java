package com.project.knowledgeassistant.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveChunks(List<Document> documents) {
        vectorStore.add(documents);
    }

    public List<Document> findByDocumentId(String documentId) {

        FilterExpressionBuilder builder =
                new FilterExpressionBuilder();

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("document lookup")
                        .topK(100)
                        .filterExpression(
                                builder.eq("documentId", documentId).build()
                        )
                        .build()
        );
    }

    public void deleteChunksByDocumentId(String documentId) {

        FilterExpressionBuilder builder =
                new FilterExpressionBuilder();

        vectorStore.delete(
                builder.eq("documentId", documentId).build()
        );
    }


}
