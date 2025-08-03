package com.security.bank.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    SAVINGS,
    CURRENT,
    PPF,
    SALARY;

    @JsonCreator
    public static AccountType fromValue(String value) {
        return valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return name();
    }
}

