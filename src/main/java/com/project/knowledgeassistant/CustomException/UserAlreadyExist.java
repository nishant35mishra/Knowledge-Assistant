package com.project.knowledgeassistant.CustomException;

public class UserAlreadyExist extends RuntimeException{

    public UserAlreadyExist(String message){
        super(message);
    }
}
