package com.example.pruebajava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ExternalServiceException extends RuntimeException {
    
    public ExternalServiceException(String message) {
        super(message);
    }
    
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ExternalServiceException serviceUnavailable(String serviceName) {
        return new ExternalServiceException("External service '" + serviceName + "' is currently unavailable");
    }
    
    public static ExternalServiceException timeout(String serviceName, int timeoutSeconds) {
        return new ExternalServiceException("Timeout calling external service '" + serviceName + "' after " + timeoutSeconds + " seconds");
    }
}
