package com.altair.expensetracker.entity;
import java.util.*;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String trip_name;
    @ManyToMany
    private List<Person> trip_participants;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Expense> expenseList;
    @Transient
    private Map<Person, BigDecimal> simplifiedDebt = new HashMap<>();
    @Transient
    private Map<Person, BigDecimal> simplifiedDebtConverted = new HashMap<>();

    public Trip() {};

    public Trip (String trip_name){
        this.trip_name = trip_name;
        this.trip_participants = new ArrayList<>();
        this.expenseList = new ArrayList<>();
    }

    public String getTripName(){
        return trip_name;
    }

    public void setTripName(String trip_name){
        this.trip_name = trip_name;
    }

    public void addPerson(String person_name){
        Person person = new Person(person_name);
        trip_participants.add(person);
    }

    //Add an Expense object to the Expense array
    public void addExpense(Expense expense){
        expenseList.add(expense);
    }

    public void addParticipants(List<Person> participants){
        for (Person person : participants) {
            if (!trip_participants.contains(person)) {
                trip_participants.add(person);
            }
        }
    }

    //Return the list of all participants
    public List<Person> getParticipantsList(){
        return trip_participants;
    }

    //Return the expenses array
    public List<Expense> getExpenseList(){
        return expenseList;
    }

    //Return the total of all expenses? 
    //public void calculateTotalExpense(){}

    public void calculateTotalExpense(){
        for (Expense expense : expenseList) {
            expense.getExpenseBalance().forEach((person, value) -> simplifiedDebt.merge(person, value, BigDecimal::add));
            expense.getExpenseBalanceConverted().forEach((person, value) -> simplifiedDebtConverted.merge(person, value, BigDecimal::add));
        }

    }  
}