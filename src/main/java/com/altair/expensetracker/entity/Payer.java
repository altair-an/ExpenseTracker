package com.altair.expensetracker.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "payer")
public class Payer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Person person;
    private BigDecimal amount; 
    @ManyToOne
    @JsonBackReference
    //@JsonIgnore 
    private Expense expense; 

    public Payer() {}
    public Payer(Person person, BigDecimal amount, Expense expense) {
        this.person = person;
        this.amount = amount;
        this.expense = expense;
    }
    
    // Getters and Setters
    public Long getID() { return id; }
    
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public void setExpense(Expense expense) { this.expense = expense; }
    public Expense getExpense() { return expense; }

    @Override
    public String toString() {
        return "Payer{" +
                "person=" + person +
                ", amount=" + amount +
                '}';
    }

}
