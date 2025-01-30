package com.example.MoneyTransferService.repository;

import com.example.MoneyTransferService.model.Amount;
import com.example.MoneyTransferService.model.Card;
import com.example.MoneyTransferService.model.Operation;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BankRepositoryImpl implements BankRepository {
    Map<String, Card> listCard = new ConcurrentHashMap<>();
    Map<Integer, Operation> listOperation = new ConcurrentHashMap<>();

    public BankRepositoryImpl() {
        Amount amount = new Amount(4000, "RUR");
        Card card1 = new Card("1111111111111111", "11/26", "123");
        card1.setAmount(amount);
        listCard.put("1111111111111111", card1);

        Card card = new Card();
        card.setCardFromNumber("2222222222222222");
        card.setAmount(new Amount(9999, "RUR"));
        listCard.put("2222222222222222", card);
    }

    @Override
    public boolean addCard(Card card) {
        if (card == null) {
            return false;
        }
        listCard.put(card.getCardFromNumber(), card);
        return listCard.containsKey(card.getCardFromNumber());
    }

    @Override
    public Optional<Card> getCard(Card card) {
        return listCard.containsKey(card.getCardFromNumber()) ? Optional.of(listCard.get(card.getCardFromNumber())) : Optional.empty();
    }

    @Override
    public boolean setOperation(Operation operation, int operationId) {
        if (listOperation.containsKey(operationId)) {
            return false;
        }
        listOperation.put(operationId, operation);
        return listOperation.containsKey(operationId);
    }

    @Override
    public Optional<Operation> getOperation(String operationId) {
        return listOperation.containsKey(Integer.valueOf(operationId)) ? Optional.of(listOperation.get(Integer.valueOf(operationId))) : Optional.empty();
    }
}
