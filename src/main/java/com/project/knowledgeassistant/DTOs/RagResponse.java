package com.project.knowledgeassistant.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RagResponse {

    String answer ;
    List<SourceResponse> sources ;

}
