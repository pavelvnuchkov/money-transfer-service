package com.example.MoneyTransferService.model;

public class CodeDto {
    private String code;
    private  String operationId;

    public CodeDto(String code, String operationId) {
        this.code = code;
        this.operationId = operationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public String toString() {
        return "CodeDto{" +
                "code='" + code + '\'' +
                ", operationId='" + operationId + '\'' +
                '}';
    }
}
