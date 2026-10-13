package com.project.knowledgeassistant.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatModel chatModel;

    public String test() {


        return chatModel.call(
                "Explain RAG in one paragraph."
        );
    }
}
