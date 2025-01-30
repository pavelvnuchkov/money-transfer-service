package com.example.MoneyTransferService.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Amount {

    private int value;

    @JsonProperty("currency")
    private String type;

    public Amount() {
    }

    public Amount(int value, String type) {
        this.value = value;
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return value == amount.value && Objects.equals(type, amount.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return "Amount{" +
                "value=" + value +
                ", type='" + type + '\'' +
                '}';
    }
}
