package com.meerkatgramv2auth.global.error.custom;

public class NotRegisteredException extends RuntimeException {
    public NotRegisteredException(String message) {
        super(message);
    }
}