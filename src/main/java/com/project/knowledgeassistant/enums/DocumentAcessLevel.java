package com.project.knowledgeassistant.enums;

import java.util.List;

public enum DocumentAcessLevel {

    PUBLIC,
    EMPLOYEE,
    MANAGER,
    ADMIN ;


    public List<DocumentAcessLevel> getAccessibleLevels() {
        return switch (this) {
            case PUBLIC ->
                    List.of(PUBLIC);

            case EMPLOYEE ->
                    List.of(PUBLIC, EMPLOYEE);

            case MANAGER ->
                    List.of(PUBLIC, EMPLOYEE, MANAGER);

            case ADMIN ->
                    List.of(PUBLIC, EMPLOYEE, MANAGER, ADMIN);
        };
    }
}
