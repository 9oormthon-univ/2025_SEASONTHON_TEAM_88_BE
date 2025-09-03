package com.eventory.server.global.apipayload.exception.handler;

import com.eventory.server.global.apipayload.code.BaseErrorCode;
import com.eventory.server.global.apipayload.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}