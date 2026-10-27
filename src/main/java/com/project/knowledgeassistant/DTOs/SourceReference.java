package com.project.knowledgeassistant.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceReference {

	private String documentId;
	
	private String documentName;
	
//	private Integer page;
	
	private String chunks ;
	
}
