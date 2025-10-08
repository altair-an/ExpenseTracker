package com.altair.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
    Example JSON for creating an expense:
 {
  "title": "Lunch",
  "amount": 3000,
  "date": "2025-10-06",
  "exchangeRate": 148,
  "participants": [
    { "id": 1, "name": "Person A" },
    { "id": 2, "name": "Person B" },
    { "id": 3, "name": "Person C" }
  ],
  "payers": [
    { "person": { "id": 1, "name": "Person A" }, "amount": 2000 },
    { "person": { "id": 2, "name": "Person B" }, "amount": 1000 }
  ],
  "splits": [
    { "person": { "id": 1, "name": "Person A" }, "amount": 1000 },
    { "person": { "id": 2, "name": "Person B" }, "amount": 1000 },
    { "person": { "id": 3, "name": "Person C" }, "amount": 1000 }
  ]
}
 */
public class ExpenseCreateDTO {
    private String title;
    private BigDecimal amount;
    private String date;
    private BigDecimal exchangeRate;
    private List<PersonDTO> participants;
    @JsonProperty("payers")
    private List<PayerDTO> payerList;
    @JsonProperty("splits")
    private List<SplitsDTO> splitsList;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public List<PersonDTO> getParticipants() { return participants; }
    public void setParticipants(List<PersonDTO> expenseParticipants) { this.participants = expenseParticipants; }
    public List<PayerDTO> getPayerList() { return payerList; }
    public void setPayerList(List<PayerDTO> payerList) { this.payerList = payerList; }
    public List<SplitsDTO> getSplitsList() { return splitsList; }
    public void setSplitsList(List<SplitsDTO> splitsList) { this.splitsList = splitsList; }
    
    // Nested DTO classes

    public static class PayerDTO {
        private PersonDTO person;
        private BigDecimal amount;
        
        // Getters and setters
        public PersonDTO getPerson() { return person; }
        public void setPerson(PersonDTO person) { this.person = person; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class SplitsDTO {
        private PersonDTO person;
        private BigDecimal amount;

        // Getters and setters
        public PersonDTO getPerson() { return person; }
        public void setPerson(PersonDTO person) { this.person = person; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}