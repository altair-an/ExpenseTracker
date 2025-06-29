package com.altair.expensetracker.entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private BigDecimal amount;
    private boolean payBack = false;  //indicates if the expense is a payback or not
    private String currencyCode;  
    private BigDecimal currencyRate;
    private String date; 

    @ManyToOne
    @JsonBackReference
    private Trip trip;
    @ManyToMany
    private List<Person> expenseParticipants; 
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payer> payerList = new ArrayList<>();
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Splits> splitsList = new ArrayList<>();
    @Transient
    private Map<Person, BigDecimal> payersMap = new HashMap<>();  //keep track of who paid the expense
    @Transient
    private Map<Person, BigDecimal> splitsMap = new HashMap<>(); //keep track of who's responsible for x amount
    @Transient
    private Map<Person, BigDecimal> expenseBalance = new HashMap<>(); // your split - your paid amount = your balance for this expense, positive means you owe money, negative means you are owed/down money
    @Transient
    private Map<Person, BigDecimal> expenseBalanceConverted = new HashMap<>(); //converted to other currency

    public Expense(){}
    public Expense(String title, BigDecimal amount, String date){
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    //expenseId getter
    public Long getID(){
        return id;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    //title setter and getter
    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    //amount setter and getter
    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    //payBack setter and getter
    public void setPayBack(boolean bool){
        this.payBack = bool;
    }

    public boolean getPayBack(){
        return payBack;
    }

    //currencyType setter and getter
    public void setCurrencyCode(String currencyCode){
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode(){
        return currencyCode;
    }

    //rate setter and getter
    public void setCurrencyRate(BigDecimal rate){
        this.currencyRate = rate;
    }

    public BigDecimal getCurrencyRate(){
        return currencyRate;
    }


    //TODO make sure format is satisfied here or outside
    //date setter and getter
    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    // Participants setter and getter
    public void setParticipants(List<Person> participants){ // Takes in a list of participants
        this.expenseParticipants = participants;
    }

    public void setParticipant(Person participant){ // Takes in a single participant
        if (expenseParticipants == null) {
            expenseParticipants = new ArrayList<>();
        }
        if (!expenseParticipants.contains(participant)) {
            expenseParticipants.add(participant); 
        }
    }

    public List<Person> getExpenseParticipants(){
        return expenseParticipants;
    }
    
    /*
    Payer setter and getter methods.
    Create a Payer object and add it to the payerList.
    Parameters:  the payer and the amount paid
    */
    public void setPayer(Person payer, BigDecimal paidAmount){
        payerList.add(new Payer(payer, paidAmount, this));
    }

    public List<Payer> getPayerList(){
        return payerList;
    }

    public Map<Person, BigDecimal> getPayerMap(){
        return payersMap;
    }
    
    /*
    Splits setter and getter methods.
    Create a Splits object and add it to the splitsList.
    Parameters: the person and their split amount that they are responsible for.
    */
    public void setSplit(Person person, BigDecimal splitAmount){
        splitsList.add(new Splits(person, splitAmount, this));
    }

    public List<Splits> getSplitsList(){
        return splitsList;
    }

    public Map<Person, BigDecimal> getSplitsMap(){
        return splitsMap;
    }

    // Getters for the expenseBalance and expenseBalanceConverted maps.
    public Map<Person, BigDecimal> getExpenseBalance(){
        return expenseBalance;
    }

    public Map<Person, BigDecimal> getExpenseBalanceConverted(){
        return expenseBalanceConverted;
    }


    /*
    Split the bill amount evenly between all participants. The for loop will iterate through 
    participants array and use the setSplit method to create new Split objects. 
    */
    public void evenSplit(){
        BigDecimal even = amount.divide(
            BigDecimal.valueOf(expenseParticipants.size()),
      3, 
            RoundingMode.HALF_UP 
        );

        for (int i = 0; i < expenseParticipants.size(); i++){
            setSplit(expenseParticipants.get(i), even); 
        }
    }

    public void updateMaps() {
        // Resetting hashmaps
        payersMap.clear();
        splitsMap.clear();
        for (Payer payer : payerList) {
            payersMap.merge(payer.getPerson(), payer.getAmount(), BigDecimal::add);
        }
        for (Splits split : splitsList) {
            splitsMap.merge(split.getPerson(), split.getAmount(), BigDecimal::add);
        }
    }

    public void calculateAll() {
        calculateExpense(); // Calculate the expense balance for each participant
        calculateIndividualBalances(); // Calculate individual balances based on the expense balance
    }

    /*
    Calculates the expenseBalance for each participant based on the payersMap and splitsMap
    Then updates the expenseBalance and expenseBalanceConverted HashMaps
    */ 
    public void calculateExpense() {
        updateMaps();
        for (Person person : expenseParticipants) {
            BigDecimal paidAmount = payersMap.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal splitAmount = splitsMap.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal diffAmount = splitAmount.subtract(paidAmount); // your split - your paid amount = amount you're still responsible for, positive means you owe money, negative means you are owed/down money
    
            expenseBalance.put(person, diffAmount);
            BigDecimal newValue = diffAmount.divide(currencyRate, 3, RoundingMode.HALF_UP);
            expenseBalanceConverted.put(person, newValue);
        }
    }

    /*
    Calculates and records exact debts between participants using the expenseBalance map (e.g., {A=-140, B=70, C=70}, where positive means borrowed and negative means lent).
    For each borrower (positive balance), finds lenders (negative balance) and assigns the minimum owed amount to the borrower's individualBalance map as {lender:amount}.
    Only expenseBalance is used and updated during this process; it is restored to original values at the end.
    */
    public void calculateIndividualBalances() {
        // Finding the borrower (positive value)
        for (Map.Entry<Person, BigDecimal> entry : expenseBalance.entrySet()) {
            Person borrowPerson = entry.getKey();
            BigDecimal borrowedAmount = entry.getValue();
            
            if (borrowedAmount.compareTo(BigDecimal.ZERO) > 0) { // Find the borrower if positive value
                // For-loop to find the lender (negative value)
                for (Map.Entry<Person, BigDecimal> entryB : expenseBalance.entrySet()) {
                    Person lenderPerson = entryB.getKey();
                    BigDecimal lentAmount = entryB.getValue();
    
                    if (borrowedAmount.compareTo(BigDecimal.ZERO) <= 0) break; // Exit this loop when the borrower's balance is settled (non positive)
    
                    if (lentAmount.compareTo(BigDecimal.ZERO) < 0) { // Find the lender, negative value
                        BigDecimal absLentAmount = lentAmount.abs(); // Get the absolute value of the lent amount and find the min
                        BigDecimal debtValue = absLentAmount.min(borrowedAmount); 
                        
                        borrowPerson.addIndividualBalance(lenderPerson, debtValue);  // Add the debt to borrowPerson's individualBalance and converted maps
                        BigDecimal convertedValue = debtValue.divide(currencyRate, 3, RoundingMode.HALF_UP); 
                        borrowPerson.addBalanceConverted(lenderPerson, convertedValue);

                        borrowedAmount = borrowedAmount.subtract(debtValue);  // Settling the the debt this round. Loop will continue until borrowedAmount is 0 or less.
                        expenseBalance.put(lenderPerson, lentAmount.add(debtValue)); // Update the expenseBalance for lenderPerson to reflect settled debt
                    }
                }
                expenseBalance.put(borrowPerson, borrowedAmount); // Update expenseBalance for borrowPerson with the remaining balance after settling debts
            }
        }
        calculateExpense(); // Recalculating the expense balance HashMap back to original values
    }
    
    //New toString to not print key:value with 0.0 because we now initialize the Map fields with 0.0 as default when adding participants. 
    public String toString(){
        String s = "ID: " + id + "  | Title: " + title + " | Date: " + date + " | Amount: " + amount + " | Rate: " + currencyRate;
        
        //Payers
        s += "\nPaid by: {";
        String data = "";
        for (Map.Entry<Person, BigDecimal> pair : payersMap.entrySet()) {
            if (pair.getValue().compareTo(BigDecimal.ZERO) != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";
        
        //Splits
        s += "\nSplits: {";
        data = "";
        for (Map.Entry<Person, BigDecimal> pair : splitsMap.entrySet()) {
            if (pair.getValue().compareTo(BigDecimal.ZERO) != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";
        
        //expenseBalance
        s += "\nBalance: {";
        data = "";
        for (Map.Entry<Person, BigDecimal> pair : expenseBalance.entrySet()) {
            if (pair.getValue().compareTo(BigDecimal.ZERO) != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";

        return s;
    }
        
}