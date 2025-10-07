package com.altair.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseCreateDTO {
    private String title;
    private BigDecimal amount;
    private String date;
    private BigDecimal exchangeRate;
    private List<PersonDTO> expenseParticipants;
    private List<PayerDTO> payerList;
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
    public List<PersonDTO> getExpenseParticipants() { return expenseParticipants; }
    public void setExpenseParticipants(List<PersonDTO> expenseParticipants) { this.expenseParticipants = expenseParticipants; }
    public List<PayerDTO> getPayerList() { return payerList; }
    public void setPayerList(List<PayerDTO> payerList) { this.payerList = payerList; }
    public List<SplitsDTO> getSplitsList() { return splitsList; }
    public void setSplitsList(List<SplitsDTO> splitsList) { this.splitsList = splitsList; }
    
    // Nested DTO classes

    public static class PersonDTO {
        private Long id;
        private String name;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

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