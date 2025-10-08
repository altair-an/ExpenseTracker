package com.altair.expensetracker.dto;
import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 
  Example JSON response for an expense:
  {
    "title": "Lunch",
    "amount": 3000.00,
    "date": "2025-10-06",
    "currencyCode": null,
    "exchangeRate": 148.00,
    "tripID": 1,
    "expenseID": 4,
    "participants": [
      "Person A",
      "Person B",
      "Person C"
    ],
    "payers": {
      "Person B": 1000.00,
      "Person A": 2000.00
    },
    "splits": {
      "Person B": 1000.00,
      "Person C": 1000.00,
      "Person A": 1000.00
    },
    "balance": {
      "Person B": 0.00,
      "Person C": 1000.00,
      "Person A": -1000.00
    },
    "convertedBalance": {
      "Person B": 0.000,
      "Person C": 6.757,
      "Person A": -6.757
    }
  }
 */

public class ExpenseDTO {
    @JsonProperty("expenseID")
    private Long id;
    private String title;
    private BigDecimal amount;
    private String date;
    private String currencyCode; 
    private BigDecimal exchangeRate; 
    private Long tripID;  
    @JsonProperty("participants")
    private List<String> participants;
    @JsonProperty("payers")
    private Map<String, BigDecimal> payersMap; // Map of participant name to amount they paid
    @JsonProperty("splits")
    private Map<String, BigDecimal> splitsMap; // Map of participant name to their split amount
    @JsonProperty("balance")
    private Map<String, BigDecimal> expenseBalance; // Map of participant name to amount owed or to receive
    @JsonProperty("convertedBalance")
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

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> expenseParticipants) { this.participants = expenseParticipants; }

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