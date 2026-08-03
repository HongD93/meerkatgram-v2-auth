package com.meerkatgramv2auth.global.error.custom.business;

import com.meerkatgramv2auth.global.error.custom.BusinessException;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;

public class AlreadyRegisteredException extends BusinessException {
    public AlreadyRegisteredException(String message) {
        super(CustomResponseCode.ALREADY_REGISTERED_ERROR, message);
    }
}
