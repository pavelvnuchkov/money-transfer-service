package com.example.MoneyTransferService.model;

public class ExceptionDto {
    private String message;
    private String id;

    public ExceptionDto(String message, String id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "message:'" + message + '\'' +
                ", 'id:" + id + '\'';
    }
}
