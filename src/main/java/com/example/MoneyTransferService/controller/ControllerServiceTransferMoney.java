package com.example.MoneyTransferService.controller;

import com.example.MoneyTransferService.model.CodeDto;
import com.example.MoneyTransferService.model.OperationDto;
import com.example.MoneyTransferService.service.ServiceBank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ControllerServiceTransferMoney {
    ServiceBank serviceBank;


    public ControllerServiceTransferMoney(ServiceBank serviceBank) {
        this.serviceBank = serviceBank;
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity transfer(@RequestBody OperationDto operationDto) {
        return serviceBank.transferMoney(operationDto);
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity get(@RequestBody CodeDto code) {
        return serviceBank.codeProcessing(code);
    }
}