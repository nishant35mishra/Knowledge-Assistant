package com.project.knowledgeassistant.globalexceptionhandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.knowledgeassistant.DTOs.ApiResponse;
import com.project.knowledgeassistant.customexception.AppException;
import com.project.knowledgeassistant.customexception.NotFound;
import com.project.knowledgeassistant.customexception.UserAlreadyExist;
import com.project.knowledgeassistant.customexception.somethingWentWrong;
import com.project.knowledgeassistant.enums.SystemCode;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        log.warn("Application exception [{}]: {}", ex.getSystemCode().getCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getSystemCode().getHttpStatus())
                .body(ApiResponse.error(ex.getSystemCode(), ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExist.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExist(UserAlreadyExist ex) {
        SystemCode code = ex.getSystemCode() != null ? ex.getSystemCode() : SystemCode.AUTH_USER_ALREADY_EXISTS;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code, ex.getMessage()));
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFound ex) {
        SystemCode code = ex.getSystemCode() != null ? ex.getSystemCode() : SystemCode.SYS_NOT_FOUND;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code, ex.getMessage()));
    }

    @ExceptionHandler(somethingWentWrong.class)
    public ResponseEntity<ApiResponse<Void>> handleSomethingWentWrong(somethingWentWrong ex) {
        SystemCode code = ex.getSystemCode() != null ? ex.getSystemCode() : SystemCode.SYS_INTERNAL_ERROR;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(field, message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(SystemCode.SYS_VALIDATION_FAILED, "Input validation failed", errors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(SystemCode.AUTH_ACCESS_DENIED, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(SystemCode.SYS_BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled internal server error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(SystemCode.SYS_INTERNAL_ERROR, ex.getMessage()));
    }
}
