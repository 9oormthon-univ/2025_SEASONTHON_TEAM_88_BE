package com.eventory.server.global.apipayload.exception.handler;

import com.eventory.server.global.apipayload.code.BaseErrorCode;
import com.eventory.server.global.apipayload.exception.GeneralException;

public class ProductHandler extends GeneralException {

    public ProductHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
