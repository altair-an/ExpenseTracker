package com.altair.expensetracker.dto;
import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpenseDTO {
    @JsonProperty("expenseId")
    private Long id;
    private String title;
    private BigDecimal amount;
    private String date;
    private String currencyCode; 
    private BigDecimal exchangeRate; 
    private Long tripID;  
    @JsonProperty("participants")
    private List<String> expenseParticipants;
    @JsonProperty("payers")
    private Map<String, BigDecimal> payersMap; // Map of participant name to amount they paid
    @JsonProperty("splits")
    private Map<String, BigDecimal> splitsMap; // Map of participant name to their split amount
    @JsonProperty("balances")
    private Map<String, BigDecimal> expenseBalance; // Map of participant name to amount owed or to receive
    @JsonProperty("convertedBalances")
    private Map<String, BigDecimal> expenseBalanceConverted; // Map of Person to amount owed or to receive in base currency


    // Getters and Setters
    public Long getID() { return id;}
    public void setID(Long id) { this.id = id;}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public List<String> getExpenseParticipants() { return expenseParticipants; }
    public void setExpenseParticipants(List<String> expenseParticipants) { this.expenseParticipants = expenseParticipants; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Long getTripID() { return tripID; }
    public void setTripID(Long tripID) { this.tripID = tripID; }

    public Map<String, BigDecimal> getPayersMap() { return payersMap; }
    public void setPayersMap(Map<String, BigDecimal> payersMap) { this.payersMap = payersMap; }

    public Map<String, BigDecimal> getSplitsMap() { return splitsMap; }
    public void setSplitsMap(Map<String, BigDecimal> splitsMap) { this.splitsMap = splitsMap; }

    public Map<String, BigDecimal> getExpenseBalance() { return expenseBalance; }
    public void setExpenseBalance(Map<String, BigDecimal> expenseBalance) { this.expenseBalance = expenseBalance; }

    public Map<String, BigDecimal> getExpenseBalanceConverted() { return expenseBalanceConverted; }
    public void setExpenseBalanceConverted(Map<String, BigDecimal> expenseBalanceConverted) { this.expenseBalanceConverted = expenseBalanceConverted; }


}