package com.project.knowledgeassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class  KnowledgeAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowledgeAssistantApplication.class, args);
	}



}
