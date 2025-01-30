package com.example.MoneyTransferService.model;

import java.util.Objects;

public class Operation {

    private Card cardDebit;
    private Card cardCredit;
    private int commission;
    private Amount amount;

    public Operation(Card cardDebit, Card cardCredit, int commission, Amount amount) {
        this.cardDebit = cardDebit;
        this.cardCredit = cardCredit;
        this.commission = commission;
        this.amount = amount;
    }

    public Card getCardDebit() {
        return cardDebit;
    }

    public void setCardDebit(Card cardDebit) {
        this.cardDebit = cardDebit;
    }

    public Card getCardCredit() {
        return cardCredit;
    }

    public void setCardCredit(Card cardCredit) {
        this.cardCredit = cardCredit;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return commission == operation.commission && Objects.equals(cardDebit, operation.cardDebit) && Objects.equals(cardCredit, operation.cardCredit) && Objects.equals(amount, operation.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardDebit, cardCredit, commission, amount);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "cardDebit=" + cardDebit +
                ", cardCredit=" + cardCredit +
                ", commission=" + commission +
                ", amount=" + amount +
                '}';
    }
}
