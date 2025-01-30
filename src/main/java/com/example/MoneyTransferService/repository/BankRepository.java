package com.example.MoneyTransferService.repository;

import com.example.MoneyTransferService.model.Card;
import com.example.MoneyTransferService.model.Operation;

import java.util.Optional;


public interface BankRepository {
    boolean addCard(Card card);

    Optional<Card> getCard(Card card);

    boolean setOperation(Operation operation, int id);

    Optional<Operation> getOperation(String operationId);
}
