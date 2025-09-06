package com.eventory.server.global.apipayload.exception.handler;

import com.eventory.server.global.apipayload.code.BaseErrorCode;
import com.eventory.server.global.apipayload.exception.GeneralException;

public class PartyHandler extends GeneralException {

    public PartyHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
