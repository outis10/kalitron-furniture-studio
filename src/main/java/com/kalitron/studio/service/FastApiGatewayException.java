package com.kalitron.studio.service;

public class FastApiGatewayException extends RuntimeException {

    public FastApiGatewayException(String message) {
        super(message);
    }

    public FastApiGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
