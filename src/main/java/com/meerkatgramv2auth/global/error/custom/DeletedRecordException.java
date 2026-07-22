package com.meerkatgramv2auth.global.error.custom;

public class DeletedRecordException extends RuntimeException {
    public DeletedRecordException(String message) {
        super(message);
    }
}