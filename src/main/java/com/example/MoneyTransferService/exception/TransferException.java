package com.example.MoneyTransferService.exception;

import com.example.MoneyTransferService.model.ExceptionDto;

public class TransferException extends IllegalArgumentException {
    public TransferException(ExceptionDto exceptionDto) {
        super(exceptionDto.toString());
    }
}
