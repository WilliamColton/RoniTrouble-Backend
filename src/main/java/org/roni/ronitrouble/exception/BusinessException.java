package org.roni.ronitrouble.exception;

public class BusinessException extends RuntimeException {

    private final ErrorInfo errorInfo;

    public BusinessException(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public int getCode() {
        return this.errorInfo.getCode();
    }

    @Override
    public String getMessage() {
        return this.errorInfo.getMessage();
    }

}
