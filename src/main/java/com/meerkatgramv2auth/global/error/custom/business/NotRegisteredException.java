package com.meerkatgramv2auth.global.error.custom.business;

import com.meerkatgramv2auth.global.error.custom.BusinessException;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;

public class NotRegisteredException extends BusinessException {
    public NotRegisteredException(String message) {
        super(CustomResponseCode.NOT_REGISTERED_ERROR, message);
    }
}
