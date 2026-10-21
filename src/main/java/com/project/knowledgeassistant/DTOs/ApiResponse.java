package com.project.knowledgeassistant.DTOs;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.knowledgeassistant.enums.SystemCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .code("SUCCESS")
                .message("Operation completed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(SystemCode systemCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(systemCode.getHttpStatus().value())
                .code(systemCode.getCode())
                .message(systemCode.getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(SystemCode systemCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(systemCode.getHttpStatus().value())
                .code(systemCode.getCode())
                .message(customMessage != null ? customMessage : systemCode.getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(SystemCode systemCode, String customMessage, T errorData) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(systemCode.getHttpStatus().value())
                .code(systemCode.getCode())
                .message(customMessage != null ? customMessage : systemCode.getDefaultMessage())
                .data(errorData)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
