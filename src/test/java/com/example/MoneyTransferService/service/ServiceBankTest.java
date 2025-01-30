package com.example.MoneyTransferService.service;

import com.example.MoneyTransferService.exception.ConfirmationException;
import com.example.MoneyTransferService.exception.InputDataException;
import com.example.MoneyTransferService.exception.TransferException;
import com.example.MoneyTransferService.model.*;
import com.example.MoneyTransferService.repository.BankRepository;
import com.example.MoneyTransferService.repository.BankRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class ServiceBankTest {


    static Card cardDebit;
    static Card cardNegativeNumber;
    static Card cardCredit;
    static OperationDto operationDto;
    static Operation operation;

    @BeforeAll
    static void beforeAll() {
        cardDebit = new Card("1111111111111111", "11/26", "123");
        cardDebit.setAmount(new Amount(9000, "RUR"));
        cardNegativeNumber = new Card("1111111111111112", "11/26", "123");
        cardCredit = new Card("2222222222222222", "11/27", "222");
        cardCredit.setAmount(new Amount(1000, "RUR"));
        operationDto = new OperationDto();
        operationDto.setCardFromNumber("1111111111111111");
        operationDto.setCardFromValidTill("11/26");
        operationDto.setCardFromCVV("123");
        operationDto.setCardToNumber("2222222222222222");
        operationDto.setAmount(new Amount(20000, "RUR"));
        operation = new Operation(cardDebit, cardCredit, 2, new Amount(200, "RUR"));
    }

    @Test
    void transferMoneyOK() {
        Card card = new Card();
        card.setCardFromNumber(operationDto.getCardToNumber());
        BankRepositoryImpl repository = Mockito.spy(BankRepositoryImpl.class);
        Mockito.when(repository.getCard(cardDebit)).thenReturn(Optional.of(cardDebit));
        Mockito.when((repository.getCard(card))).thenReturn(Optional.of(cardCredit));
        ServiceBank serviceBank = new ServiceBank(repository);
        serviceBank.transferMoney(operationDto);
        Assertions.assertEquals(Optional.of(operation), repository.getOperation("1"));
        Assertions.assertTrue(repository.getOperation("1").isPresent());
        Assertions.assertFalse(repository.getOperation("2").isPresent());
    }

    @Test
    void transferMoneyRefactorArgumentPermissionDebit() {
        Card cardExpectedDebit = new Card("1111111111111111", "11/26", "123");
        Card cardExpectedCredit = new Card();
        cardExpectedCredit.setCardFromNumber(operationDto.getCardToNumber());
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(cardDebit)).thenReturn(Optional.of(cardDebit));
        Mockito.when((repository.getCard(cardExpectedCredit))).thenReturn(Optional.of(cardCredit));
        Mockito.when(repository.setOperation(Mockito.any(), Mockito.eq(1))).thenReturn(true);

        ServiceBank serviceBank = new ServiceBank(repository);
        serviceBank.transferMoney(operationDto);
        ArgumentCaptor<Card> argumentCard = ArgumentCaptor.forClass(Card.class);
        Mockito.verify(repository, Mockito.times(2)).getCard(argumentCard.capture());
        Assertions.assertEquals(cardExpectedDebit, argumentCard.getAllValues().get(0));
        Assertions.assertEquals(cardExpectedCredit, argumentCard.getAllValues().get(1));
    }

    @Test
    void transferMoneyRefactorArgumentSetOperation() {
        Card cardExpectedDebit = new Card("1111111111111111", "11/26", "123");
        Card cardExpectedCredit = new Card();
        cardExpectedCredit.setCardFromNumber(operationDto.getCardToNumber());
        BankRepository repository = Mockito.spy(BankRepository.class);
        Mockito.when(repository.getCard(cardDebit)).thenReturn(Optional.of(cardDebit));
        Mockito.when((repository.getCard(cardExpectedCredit))).thenReturn(Optional.of(cardCredit));
        Mockito.when(repository.setOperation(operation, 1)).thenReturn(true);

        ServiceBank serviceBank = new ServiceBank(repository);
        serviceBank.transferMoney(operationDto);
        ArgumentCaptor<Operation> argumentCard = ArgumentCaptor.forClass(Operation.class);
        ArgumentCaptor<Integer> argumentId = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(repository).setOperation(argumentCard.capture(), argumentId.capture());
        Assertions.assertEquals(operation, argumentCard.getValue());
    }

    @Test
    void transferMoneyNotCardCredit() {
        Card card = new Card();
        card.setCardFromNumber(operationDto.getCardToNumber());
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(cardDebit)).thenReturn(Optional.of(cardDebit));
        Mockito.when((repository.getCard(card))).thenReturn(Optional.empty());
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.transferMoney(operationDto);
        });
    }

    @Test
    void transferMoneyNotCardDebit() {
        Card card = new Card();
        card.setCardFromNumber(operationDto.getCardToNumber());
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(cardDebit)).thenReturn(Optional.empty());
        Mockito.when((repository.getCard(card))).thenReturn(Optional.of(cardCredit));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.transferMoney(operationDto);
        });
    }

    @Test
    void codeProcessingOK() {
        CodeDto codeDto = new CodeDto("0000", "1");
        BankRepository bankRepository = Mockito.spy(BankRepository.class);
        Mockito.when(bankRepository.getOperation(codeDto.getOperationId())).thenReturn(Optional.of(operation));
        Mockito.when(bankRepository.getCard(cardDebit)).thenReturn(Optional.of(cardDebit));
        ServiceBank serviceBank = new ServiceBank(bankRepository);
        serviceBank.codeProcessing(codeDto);
        Assertions.assertEquals(8798, bankRepository.getCard(cardDebit).get().getAmount().getValue());
    }

    @Test
    void codeProcessingBlanc() {
        CodeDto codeDto = new CodeDto("", "1");
        ServiceBank serviceBank = new ServiceBank(Mockito.mock(BankRepository.class));
        Assertions.assertThrows(ConfirmationException.class, () -> {
            serviceBank.codeProcessing(codeDto);
        });
    }

    @Test
    void codeProcessingNotLength() {
        CodeDto codeDto = new CodeDto("000", "1");
        ServiceBank serviceBank = new ServiceBank(Mockito.mock(BankRepository.class));
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.codeProcessing(codeDto);
        });
    }

    @Test
    void permissionCardDebitNotCard() {
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.empty());
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.permissionCardDebit(cardDebit, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardDebitCardWrongTill() {
        Card cardNegativeTill = new Card("1111111111111111", "11/25", "123");
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardDebit));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.permissionCardDebit(cardNegativeTill, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardDebiCardWrongCVV() {
        Card cardNegativeCVV = new Card("1111111111111111", "11/26", "122");
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardDebit));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.permissionCardDebit(cardNegativeCVV, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardDebiCardWrongAmountType() {
        Card cardNegativeAmountType = new Card("1111111111111111", "11/26", "122");
        cardNegativeAmountType.setAmount(new Amount(2000, "EUR"));
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardNegativeAmountType));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(TransferException.class, () -> {
            serviceBank.permissionCardDebit(cardNegativeAmountType, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardDebiCardNotMany() {
        Card cardNegativeNotMany = new Card("1111111111111111", "11/26", "122");
        cardNegativeNotMany.setAmount(new Amount(100, "RUR"));
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardNegativeNotMany));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(TransferException.class, () -> {
            serviceBank.permissionCardDebit(cardNegativeNotMany, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardDebitOK() {
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardDebit));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertTrue(serviceBank.permissionCardDebit(cardDebit, new Amount(200, "RUR")).isPresent());
    }

    @Test
    void permissionCardCreditNotCard() {
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.empty());
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(InputDataException.class, () -> {
            serviceBank.permissionCardCredit(cardCredit, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardCreditWrongAmountType() {
        Card cardCreditExpected = new Card("2222222222222222", "11/27", "222");
        cardCreditExpected.setAmount(new Amount(100, "EUR"));
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardCreditExpected));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertThrows(TransferException.class, () -> {
            serviceBank.permissionCardCredit(cardCredit, new Amount(200, "RUR"));
        });
    }

    @Test
    void permissionCardCreditOK() {
        BankRepository repository = Mockito.mock(BankRepository.class);
        Mockito.when(repository.getCard(Mockito.any())).thenReturn(Optional.of(cardDebit));
        ServiceBank serviceBank = new ServiceBank(repository);
        Assertions.assertTrue(serviceBank.permissionCardCredit(cardDebit, new Amount(200, "RUR")).isPresent());
    }

}