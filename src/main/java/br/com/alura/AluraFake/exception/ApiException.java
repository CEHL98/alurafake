package br.com.alura.AluraFake.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String userMessage;

    public ApiException( String userMessage,HttpStatus status) {
        super(userMessage);
        this.status = status;
        this.userMessage = userMessage;
    }

    public ApiException(HttpStatus status, String userMessage, String technicalMessage) {
        super(technicalMessage);
        this.status = status;
        this.userMessage = userMessage;
    }

    public ApiException(HttpStatus status, String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.status = status;
        this.userMessage = userMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getUserMessage() {
        return userMessage;
    }
}