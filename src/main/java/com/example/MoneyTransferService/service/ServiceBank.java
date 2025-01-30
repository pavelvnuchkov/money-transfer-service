package com.example.MoneyTransferService.service;

import com.example.MoneyTransferService.exception.ConfirmationException;
import com.example.MoneyTransferService.exception.InputDataException;
import com.example.MoneyTransferService.exception.TransferException;
import com.example.MoneyTransferService.logger.Logger;
import com.example.MoneyTransferService.model.*;
import com.example.MoneyTransferService.repository.BankRepository;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServiceBank {
    BankRepository bankRepository;
    Logger logger;
    private static AtomicInteger id;

    public ServiceBank(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
        this.logger = new Logger("logOperation");
        id = new AtomicInteger();
    }

    public ResponseEntity transferMoney(OperationDto operationDto) {
        id.incrementAndGet();
        logger.addMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss F")));
        logger.addMessage("Карта списания: " + operationDto.getCardFromNumber());
        logger.addMessage("Карта зачисления: " + operationDto.getCardToNumber());
        Amount amount = new Amount(operationDto.getAmount().getValue() / 100, operationDto.getAmount().getType());
        logger.addMessage("Сумма: " + amount.getValue());
        int commission = amount.getValue() / 100;
        logger.addMessage("Комиссия: " + commission);
        Card cardDebitDto = new Card(operationDto.getCardFromNumber(), operationDto.getCardFromValidTill(), operationDto.getCardFromCVV());
        Card cardCreditDto = new Card();
        cardCreditDto.setCardFromNumber(operationDto.getCardToNumber());
        Optional<Card> optionalCardDebit = permissionCardDebit(cardDebitDto, amount);
        Optional<Card> optionalCardCredit = permissionCardCredit(cardCreditDto, amount);
        if (optionalCardDebit.isPresent() && optionalCardCredit.isPresent()) {
            if (!bankRepository.setOperation(new Operation(optionalCardDebit.get(), optionalCardCredit.get(), commission, amount), id.intValue())) {
                logger.addMessage("Операция заблокирована!");
                throw new InputDataException(new ExceptionDto("Операция заблокирована!", id.toString()));
            }
            return new ResponseEntity<>(new AnswerOkDto(id.toString()), HttpStatus.OK);
        }
        logger.addMessage("Операция заблокирована!");
        return new ResponseEntity<>(new ExceptionDto("Операция заблокирована!", id.toString()), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity codeProcessing(CodeDto code) {
        if (code.getCode().isBlank() || code.getOperationId().isBlank()) {
            throw new ConfirmationException(new ExceptionDto("Ошибка id", id.toString()));
        } else if (code.getCode().length() != 4) {
            throw new InputDataException(new ExceptionDto("Неправильный id", code.getOperationId()));
        }
        Optional<Operation> operation = bankRepository.getOperation(code.getOperationId());
        if (operation.isPresent()) {
            int balanceDebit = operation.get().getCardDebit().getAmount().getValue();
            operation.get().getCardDebit().getAmount().setValue(balanceDebit - operation.get().getAmount().getValue() - operation.get().getCommission());
            int balanceCredit = operation.get().getCardCredit().getAmount().getValue();
            operation.get().getCardCredit().getAmount().setValue(balanceCredit + operation.get().getAmount().getValue());
            return new ResponseEntity(new AnswerOkDto(id.toString()) , HttpStatus.OK);
        }
        return new ResponseEntity<>(new ExceptionDto("Операция заблокирована!", id.toString()), HttpStatus.BAD_REQUEST);
    }

    public Optional<Card> permissionCardDebit(Card card, Amount amount) {
        Optional<Card> optionalCardDebit = bankRepository.getCard(card);
        if (optionalCardDebit.isPresent()) {
            if (!optionalCardDebit.get().equals(card)) {
                logger.addMessage("Ошибка - Данные карты списания не верны!");
                throw new InputDataException(new ExceptionDto("Данные карты не верны!", id.toString()));
            }
            if (!optionalCardDebit.get().getAmount().getType().equals(amount.getType()) ||
                    optionalCardDebit.get().getAmount().getValue() < (amount.getValue() + amount.getValue() / 100)) {
                logger.addMessage("Ошибка - Недостаточно средств на карте списания");
                throw new TransferException(new ExceptionDto("Операция невозможна!", id.toString()));
            }
        } else {
            logger.addMessage("Ошибка - Карты списания не существует!");
            throw new InputDataException(new ExceptionDto("Такой карты не существует!", id.toString()));
        }
        return optionalCardDebit;
    }

    public Optional<Card> permissionCardCredit(Card card, Amount amount) {
        Optional<Card> optionalCardCredit = bankRepository.getCard(card);
        if (optionalCardCredit.isPresent()) {
            if (!optionalCardCredit.get().getAmount().getType().equals(amount.getType())) {
                logger.addMessage("Ошибка - Операция невозможна из-за карты " + optionalCardCredit.get().getCardFromNumber());
                throw new TransferException(new ExceptionDto("Операция невозможна!", id.toString()));
            }
        } else {
            logger.addMessage("Ошибка - Карты зачисления не существует!");
            throw new InputDataException(new ExceptionDto("Такой карты не существует!", id.toString()));
        }
        return optionalCardCredit;
    }
}
