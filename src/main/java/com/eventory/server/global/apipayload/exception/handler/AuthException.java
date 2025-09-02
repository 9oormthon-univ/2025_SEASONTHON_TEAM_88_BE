package com.eventory.server.global.apipayload.exception.handler;

import com.eventory.server.global.apipayload.code.BaseErrorCode;
import com.eventory.server.global.apipayload.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
