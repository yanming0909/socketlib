package com.patent.socket.client.helper.validation;

/**
 */
public class BaseValidationHelper implements AbsValidationHelper {
    @Override
    public boolean execute(byte[] msg) {
        return true;
    }
}
