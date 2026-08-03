package com.vishwesh.jwt_authentication_series_01.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{

    private final HttpStatus status;
//    private final int status;


    // Using HttpStatus
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;  //.value();
    }

    // Using integer status code
    public ApiException(int statusCode, String message) {
        super(message);
        this.status = HttpStatus.valueOf(statusCode);
//        this.status = statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
