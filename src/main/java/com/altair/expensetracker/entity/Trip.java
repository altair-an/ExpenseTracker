package com.altair.expensetracker.entity;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.math.*;

@Entity
@Table(name = "trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tripName;
    @ManyToMany
    private List<Person> tripParticipants;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Expense> expenseList;
    @Transient
    private Map<Person, BigDecimal> simpleDebt = new HashMap<>();  // Simplified net balances for each person in the trip
    @Transient
    private Map<Person, BigDecimal> simpleDebtConverted = new HashMap<>(); // Simplified net balances in converted currency

    public Trip() {};
    public Trip(String trip_name){
        this.tripName = trip_name;
        this.tripParticipants = new ArrayList<>();
        this.expenseList = new ArrayList<>();
    }

    // Getters and Setters
    public String getName(){ return tripName; }
    public void setName(String trip_name){ this.tripName = trip_name; }

    public void addPerson(String person_name){
        Person person = new Person(person_name);
        tripParticipants.add(person);
    }
    public void addParticipants(List<Person> participants){
        for (Person person : participants) {
            if (!tripParticipants.contains(person)) {
                tripParticipants.add(person);
            }
        }
    }
    public List<Person> getParticipants(){ return tripParticipants; }

    public void addExpense(Expense expense){expenseList.add(expense);}
    public List<Expense> getExpenseList(){return expenseList;}

    public Long getID() {return id;}
    public void setID(Long id) {this.id = id;}

    public Map<Person, BigDecimal> getSimpleDebt() {return simpleDebt;}
    public Map<Person, BigDecimal> getSimpleDebtConverted() {return simpleDebtConverted;}

    // Calculate the total expense for the trip by aggregating expenses from all associated Expense entities
    // This method updates the tripSimplifiedDebt and tripSimplifiedDebtConverted maps
    public void calculateTotalExpense(){
        for (Expense expense : expenseList) {
            expense.getExpenseBalance().forEach((person, value) -> simpleDebt.merge(person, value, BigDecimal::add));
            expense.getExpenseBalanceConverted().forEach((person, value) -> simpleDebtConverted.merge(person, value, BigDecimal::add));
        }
    }  
}