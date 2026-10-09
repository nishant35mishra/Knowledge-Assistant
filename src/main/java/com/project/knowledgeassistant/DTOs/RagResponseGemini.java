package com.project.knowledgeassistant.DTOs;

import java.util.List;

public record RagResponseGemini(String answer ,
                                boolean answerFound ,
                                List<SourceResponse> source
                                 ) {



}
