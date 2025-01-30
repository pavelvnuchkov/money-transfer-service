package com.example.MoneyTransferService.exception;

import com.example.MoneyTransferService.model.ExceptionDto;

public class InputDataException extends IllegalArgumentException {
    public InputDataException(ExceptionDto exceptionDto) {
        super(exceptionDto.toString());
    }

}
