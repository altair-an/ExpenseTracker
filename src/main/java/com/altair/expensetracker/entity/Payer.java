package com.altair.expensetracker.entity;

import java.math.BigDecimal;
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
    private Expense expense; 

    public Payer() {}
    
    public Payer(Person person, BigDecimal amount, Expense expense) {
        this.person = person;
        this.amount = amount;
        this.expense = expense;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payer{" +
                "person=" + person +
                ", amount=" + amount +
                '}';
    }

}
