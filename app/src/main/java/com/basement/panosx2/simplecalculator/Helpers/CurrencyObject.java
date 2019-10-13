package com.basement.panosx2.simplecalculator.Helpers;

public class CurrencyObject {
    private String currency;
    private double rate;

    public CurrencyObject(String currency, double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public double getRate() {
        return rate;
    }
}
