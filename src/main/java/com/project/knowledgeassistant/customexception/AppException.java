package com.project.knowledgeassistant.customexception;

import com.project.knowledgeassistant.enums.SystemCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final SystemCode systemCode;

    public AppException(SystemCode systemCode) {
        super(systemCode.getDefaultMessage());
        this.systemCode = systemCode;
    }

    public AppException(SystemCode systemCode, String message) {
        super(message != null ? message : systemCode.getDefaultMessage());
        this.systemCode = systemCode;
    }

    public AppException(SystemCode systemCode, String message, Throwable cause) {
        super(message != null ? message : systemCode.getDefaultMessage(), cause);
        this.systemCode = systemCode;
    }
}
