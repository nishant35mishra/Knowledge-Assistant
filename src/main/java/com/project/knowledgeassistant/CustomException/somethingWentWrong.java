package com.project.knowledgeassistant.CustomException;

public class somethingWentWrong extends RuntimeException{
    public somethingWentWrong(String message) {
        super(message);
    }
}
