package com.project.knowledgeassistant.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RagResponse {

    String answer ;
    List<SourceResponse> sources ;

}
