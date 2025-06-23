package com.altair.expensetracker;
import java.math.BigDecimal;

public class Splits {
    
    private Person person;
    private BigDecimal amount; // Amount owed by the person

    public Splits(Person person, BigDecimal amount) {
        this.person = person;
        this.amount = amount;
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
}
