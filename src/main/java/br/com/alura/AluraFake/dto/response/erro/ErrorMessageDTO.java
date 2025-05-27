package br.com.alura.AluraFake.dto.response.erro;

import java.time.Instant;

public class ErrorMessageDTO {

    private Integer statusCode;
    private Instant timestamp;
    private String error;
    private String description;

    public ErrorMessageDTO() {
        this.timestamp = Instant.now();
    }

    public ErrorMessageDTO(Integer statusCode, String error, String description) {
        this();
        this.statusCode = statusCode;
        this.error = error;
        this.description = description;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
