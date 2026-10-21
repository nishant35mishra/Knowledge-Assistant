package com.project.knowledgeassistant.customexception;

import com.project.knowledgeassistant.enums.SystemCode;

public class somethingWentWrong extends AppException {

    public somethingWentWrong(String message) {
        super(SystemCode.SYS_INTERNAL_ERROR, message);
    }

    public somethingWentWrong(SystemCode systemCode, String message) {
        super(systemCode, message);
    }
}
