package com.tradewise.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a request is syntactically valid but violates a business rule
 * (e.g. password mismatch, insufficient cash, expired OTP).
 */
public class BusinessRuleException extends ApplicationException {

    public BusinessRuleException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
