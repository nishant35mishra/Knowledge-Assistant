package com.project.knowledgeassistant.CustomException;

public class NotFound extends RuntimeException{

    public NotFound(String message){
        super(message);
    }
}
