package com.example.MoneyTransferService.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ResponseStatus(HttpStatus.OK)
public class AnswerOkDto implements AnswerOk {
    private String operationId;

    public AnswerOkDto(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerOkDto that = (AnswerOkDto) o;
        return Objects.equals(operationId, that.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(operationId);
    }

    @Override
    public String toString() {
        return "AnswerOkDto{" +
                "operationId='" + operationId + '\'' +
                '}';
    }
}
