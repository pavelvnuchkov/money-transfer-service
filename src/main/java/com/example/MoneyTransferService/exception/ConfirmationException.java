package com.example.MoneyTransferService.exception;

import com.example.MoneyTransferService.model.ExceptionDto;

public class ConfirmationException extends IllegalArgumentException {
    public ConfirmationException(ExceptionDto exceptionDto) {
        super(exceptionDto.toString());
    }
}
