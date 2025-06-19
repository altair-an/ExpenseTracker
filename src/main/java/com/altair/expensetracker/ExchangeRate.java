package com.altair.expensetracker;
import java.time.LocalDate;

public class ExchangeRate {
    private String currencyType;
    private double rate;
    private LocalDate date;

    public ExchangeRate(String currencyType, double rate, LocalDate date) {
        this.currencyType = currencyType;
        this.rate = rate;
        this.date = date;
    }

    // Method to convert foreign value to USD
    //Look up "(your end currency) to (currency you want to convert)". Example, USD to JPY
    public double convertToUSD(double foreignValue) {
        double value = foreignValue / rate;
        return Double.parseDouble(String.format("%.2f", value));
    }

    public void setDate(LocalDate date){
        this.date = date;
    }
    
    public LocalDate getDate() {
        return date;
    }

    public void setRate(double rate){
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setCurrencyType(String currencyType){
        this.currencyType = currencyType;
    }

    public String getCurrencyType() {
        return currencyType;
    }
}