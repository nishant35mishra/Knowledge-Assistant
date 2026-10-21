package com.project.knowledgeassistant.enums;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum SystemCode {

    // === Document Management (DOC_001 -> DOC_099) ===
    DOC_INVALID_FILE_TYPE("DOC_001", "File type not supported. Only PDF, DOCX and TXT are allowed.", HttpStatus.BAD_REQUEST),
    DOC_EMPTY_FILE("DOC_002", "Uploaded file is empty or contains no readable text.", HttpStatus.BAD_REQUEST),
    DOC_STORAGE_FAILED("DOC_003", "Failed to store or process uploaded document.", HttpStatus.INTERNAL_SERVER_ERROR),
    DOC_NOT_FOUND("DOC_004", "Document not found with the given ID.", HttpStatus.NOT_FOUND),
    DOC_ACCESS_DENIED("DOC_005", "You do not have permission to access this document.", HttpStatus.FORBIDDEN),
    DOC_INVALID_ACCESS_LEVEL("DOC_006", "Invalid document access level specified.", HttpStatus.BAD_REQUEST),
    DOC_CONVERSION_FAILED("DOC_007", "Document format conversion failed.", HttpStatus.INTERNAL_SERVER_ERROR),

    // === Authentication & User (AUTH_001 -> AUTH_099) ===
    AUTH_INVALID_CREDENTIALS("AUTH_001", "Invalid email or password.", HttpStatus.UNAUTHORIZED),
    AUTH_USER_ALREADY_EXISTS("AUTH_002", "User with this email already exists.", HttpStatus.BAD_REQUEST),
    AUTH_TOKEN_EXPIRED_OR_REVOKED("AUTH_003", "Token is expired, revoked, or invalid.", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_DENIED("AUTH_004", "Access denied. Insufficient permissions for this resource.", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_NOT_ACTIVE("AUTH_005", "Account is not active or pending approval.", HttpStatus.FORBIDDEN),
    AUTH_USER_NOT_FOUND("AUTH_006", "User with the given ID or email does not exist.", HttpStatus.NOT_FOUND),
    AUTH_ROLE_NOT_FOUND("AUTH_007", "Requested role does not exist.", HttpStatus.NOT_FOUND),
    AUTH_SELF_ADMIN_FORBIDDEN("AUTH_008", "Users cannot register themselves as ADMIN.", HttpStatus.FORBIDDEN),
    AUTH_APPROVAL_FORBIDDEN("AUTH_009", "Unauthorized approval or rejection action for this user role.", HttpStatus.FORBIDDEN),

    // === RAG & Chat (RAG_001 -> RAG_099) ===
    RAG_QUESTION_BLANK("RAG_001", "Chat question cannot be blank or empty.", HttpStatus.BAD_REQUEST),
    RAG_NO_INFO_FOUND("RAG_002", "I could not find relevant information in the provided documents.", HttpStatus.OK),
    RAG_CHAT_SESSION_NOT_FOUND("RAG_003", "Chat session not found or access denied.", HttpStatus.NOT_FOUND),
    RAG_SERVICE_ERROR("RAG_004", "AI or Vector search service error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),

    // === Common / System (SYS_001 -> SYS_099) ===
    SYS_INTERNAL_ERROR("SYS_001", "Internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),
    SYS_NOT_FOUND("SYS_002", "Requested resource not found.", HttpStatus.NOT_FOUND),
    SYS_BAD_REQUEST("SYS_003", "Bad request or invalid parameters provided.", HttpStatus.BAD_REQUEST),
    SYS_VALIDATION_FAILED("SYS_004", "Input validation failed.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    SystemCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
