package com.altair.expensetracker.entity;
import java.math.BigDecimal;
import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @Transient
    private Map<Person, BigDecimal> individualBalance = new HashMap<>(); //Storing exact debt so you know from whom and how much you owe
    
    @Transient
    private Map<Person, BigDecimal> balanceConverted = new HashMap<>();

    public Person(){}
    public Person(String name){
        this.name = name;
    }

    public Long getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    // Used in Expense's calculateIndividualBalance method
    public void addIndividualBalance(Person person, BigDecimal amount){
        individualBalance.merge(person, amount, BigDecimal::add);
    }

    public Map<Person, BigDecimal> getIndividualBalance(){
        return individualBalance;
    }

    // Same but for converted amount
    public void addBalanceConverted(Person person, BigDecimal amount){
        balanceConverted.merge(person, amount, BigDecimal::add);
    }
    
    public Map<Person, BigDecimal> getBalanceConverted(){
        return balanceConverted;
    }

    //For debugging while testing the expense class printing out results
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id; // or use name.equals(person.name) if name is unique
        //return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(name); // or Objects.hash(id) 
        return Objects.hash(id); // if id is unique
    }


}
