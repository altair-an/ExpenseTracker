package com.altair.expensetracker;
import java.math.BigDecimal;
import java.util.*;

public class Person {
    private static int counter;
    private int id;
    private String name;
    private Map<Person, BigDecimal> individualBalance = new HashMap<>(); //Storing exact debt so you know from whom and how much you owe
    private Map<Person, BigDecimal> balanceConverted = new HashMap<>();

    public Person(){}
    public Person(String name){
        this.name = name;
        counter++;
        id = counter;

    }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

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
        //return id == person.id; // or use name.equals(person.name) if name is unique
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); // or Objects.hash(id) 
    }


}
