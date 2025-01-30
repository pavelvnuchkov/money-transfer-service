package com.example.MoneyTransferService.repository;

import com.example.MoneyTransferService.model.Amount;
import com.example.MoneyTransferService.model.Card;
import com.example.MoneyTransferService.model.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class BankRepositoryTest {

    static BankRepositoryImpl repository;
    static Card cardDebit;
    static Card cardCredit;
    static Card cardNegativeNumber;
    static Operation operation;

    @BeforeAll
    static void beforeAll() {
        repository = new BankRepositoryImpl();
        cardDebit = new Card("1111111111111111", "11/26", "123");
        cardCredit = new Card("2222222222222222", "11/27", "222");
        cardNegativeNumber = new Card("1111111111111112", "11/26", "123");
        operation = new Operation(cardDebit, cardCredit, 10, new Amount(1000, "RUR"));
    }


    @Test
    void addCard() {
        Assertions.assertTrue(repository.addCard(cardDebit));
        Assertions.assertFalse(repository.addCard(null));
    }

    @Test
    void getCard() {
        Assertions.assertEquals(Optional.of(cardDebit), repository.getCard(cardDebit));
    }

    @Test
    void getCardNegative() {
        Assertions.assertEquals(Optional.empty(), repository.getCard(cardNegativeNumber));
    }

    @Test
    void setOperation() {
        Assertions.assertTrue(repository.setOperation(operation, 1));
        Assertions.assertFalse(repository.setOperation(operation, 1));
    }

    @Test
    void getOperation() {
        Assertions.assertEquals(Optional.of(operation), repository.getOperation("1"));
        Assertions.assertEquals(Optional.empty(), repository.getOperation("2"));
    }



}

