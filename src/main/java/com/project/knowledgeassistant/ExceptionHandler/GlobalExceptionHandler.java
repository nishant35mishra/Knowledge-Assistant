package com.project.knowledgeassistant.ExceptionHandler;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.knowledgeassistant.CustomException.NotFound;
import com.project.knowledgeassistant.CustomException.UserAlreadyExist;
import com.project.knowledgeassistant.CustomException.somethingWentWrong;
import org.springframework.security.access.AccessDeniedException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExist.class)
    public ResponseEntity<String> handleException(UserAlreadyExist ex){

        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String,String>> handleException2(MethodArgumentNotValidException ex){
        HashMap<String,String> map = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((err)->{

            String message = (String)err.getDefaultMessage();
            String field =  ((FieldError)err).getField();

            map.put(field, message);
        }) ;
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<String> handleException5(NotFound ex){

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(somethingWentWrong.class)
    public ResponseEntity<String> handleException4(somethingWentWrong ex){

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException3(RuntimeException ex){

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN); // <-- Returns 403 Forbidden!
    }

}
