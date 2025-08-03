package com.security.bank.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InvestmentType {
    GOLD,
    STOCKS,
    MUTUAL_FUND,
    FIXED_DEPOSITS;

    @JsonCreator
    public static InvestmentType fromValue(String value) {
        return valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return name();
    }
}
