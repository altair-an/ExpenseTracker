package com.altair.expensetracker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Expense {
    private static int counter;
    private int id;
    private String title;
    private BigDecimal amount;
    private boolean payBack = false;  //indicates if the expense is a payback or not
    private String currencyType;  
    private BigDecimal currencyRate;
    private String date; 
    private List<Person> participants; 
    private List<Payer> payerList = new ArrayList<>();
    private List<Splits> splitsList = new ArrayList<>();
    private Map<Person, BigDecimal> paidBy = new HashMap<>();  //keep track of who paid the expense
    private Map<Person, BigDecimal> splits = new HashMap<>(); //keep track of who's responsible for x amount
    private Map<Person, BigDecimal> expenseBalance = new HashMap<>(); //paidby minus splits
    private Map<Person, BigDecimal> expenseBalanceConverted = new HashMap<>(); //converted to other currency

    public Expense(){}
    public Expense(String title, BigDecimal amount, String date){
        this.title = title;
        this.amount = amount;
        this.date = date;
        counter++;
        id = counter;
    }

    //expenseId getter
    public int getID(){
        return id;
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
    public void setCurrencyType(String currencyType){
        this.currencyType = currencyType;
    }

    public String getCurrencyType(){
        return currencyType;
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

    //participants setter and getter
    public void setParticipants(List<Person> participants){
        this.participants = participants;
        BigDecimal zero = BigDecimal.ZERO; // Using BigDecimal for precision

        for (Person person : participants) {
            paidBy.put(person, zero); // Initialize paidBy with zero for each participant
            splits.put(person, zero); // Initialize splits with zero for each participant
        }
    }

    public void setParticipant(Person participant){
        if (participants == null) {
            participants = new ArrayList<>();
        }
        BigDecimal zero = BigDecimal.ZERO; // Using BigDecimal for precision

        if (!participants.contains(participant)) {
            participants.add(participant); 
            paidBy.put(participant, zero); // Initialize paidBy with zero for each participant
            splits.put(participant, zero); // Initialize splits with zero for each participant
        }
    }

    public List<Person> getParticipants(){
        return participants;
    }
    
    /*
    Setter method to set payer(s) with amount paid for this expense.
    Parameters:  the payer and the amount paid
    */
    public void setPayer(Person payer, BigDecimal paidAmount){
        payerList.add(new Payer(payer, paidAmount));
    }


    public Map<Person, BigDecimal> getPayerMap(){
        return paidBy;
    }
    
    /*
    Setter method to set a person and their split of the bill that they're responsible for.
    Parameters: the person and their split amount
    */
    public void setSplit(Person person, BigDecimal splitAmount){
        splitsList.add(new Splits(person, splitAmount));
    }

    public Map<Person, BigDecimal> getSplitsMap(){
        return splits;
    }

        //Getter for the expenseBalance HashMap
    public Map<Person, BigDecimal> getExpenseBalance(){
        return expenseBalance;
    }

    // This map contains the converted expenseBalance for each participant based on the exchange rate.
    public Map<Person, BigDecimal> getExpenseBalanceConverted(){
        return expenseBalanceConverted;
    }


    /*
    Split the bill amount evenly between all participants. The for loop will iterate through 
    participants array and use the setSplit method. 
    */
    public void evenSplit(){
        BigDecimal even = amount.divide(
            BigDecimal.valueOf(participants.size()),
      3, // scale: number of decimal places
            RoundingMode.HALF_UP // rounding mode
        );

        for (int i = 0; i < participants.size(); i++){
            //splits.put(participants.get(i), even); // using the Map's put method directly
            setSplit(participants.get(i), even); // using custom setter method
        }
    }

    public void updateMaps() {
        //resetting hashmaps
        paidBy.clear();
        splits.clear();
        for (Payer payer : payerList) {
            paidBy.merge(payer.getPerson(), payer.getAmount(), BigDecimal::add);
        }
        for (Splits split : splitsList) {
            splits.merge(split.getPerson(), split.getAmount(), BigDecimal::add);
        }
    }

    /*
    Apparently HashMap got a forEach method! But all the logic has to be done within the method parenthesis (). 
    splits.forEach(   (key, value) -> {some logic statements}   );  //extra space is for readability
    */


    // Calculates the expenseBalance for each participant based on the paidBy and splits HashMaps in this class.
    // Updates the expenseBalance and expenseBalanceConverted HashMaps.
    public void calculateExpense() {
        updateMaps();
        for (Person person : participants) {
            BigDecimal paidAmount = paidBy.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal splitAmount = splits.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal diffAmount = splitAmount.subtract(paidAmount);
    
            expenseBalance.put(person, diffAmount);
            BigDecimal newValue = diffAmount.divide(currencyRate, 3, RoundingMode.HALF_UP);
            expenseBalanceConverted.put(person, newValue);
        }
    }

    /*
    Calculates the exact debt using the expenseBalance HashMap like {A=-140, B=70, C=70}.
    Positive balance means that you borrowed that amount (like a credit card). 
    Negative balance means that someone owes you money and the value is the amount you (the lender) have lent.
    In the given example above, A lent 140 while B and C both borrowed 70 each. 
    Each Person object has HashMap<person, value> field called 'exactDebt' and this method will update exactDebt with {lender:amount}.
    For instance, this method will add {A:70} to person B and C's exactDebt since they both owe A an amount of 70.

    This method has an outside for-loop that iterates through 'expenseBalance' Map to find the borrower (positive value) then the inside for-loop that iterates to find the lender (negative value).
    In all cases, the amount that needs to be settled is always the smaller value between the two values abs(lender) and borrower. 
    This smallest value between the two will then is used to update borrower's exactDebt and update the lender's expenseBalance Map. 
    */

    // Calculates the exact debt for each borrower based on the expenseBalance HashMap ONLY. No other Map is used in this method.
    // This method assumes that the expenseBalance HashMap has been calculated before calling this method. credit Map will be updated along the way and will be reset to original values after this method is done
    // This method will update the borrower's Person class exactDebt Map with the lender and the amount owed - {lender:amount}.
    public void calculateIndividualBalances() {
        // For-loop to find the borrower (positive value)
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
                        // Get the smaller value between the lender and borrower. Need to abs value of the lender since it's negative.
                        // Ex. [A = -140 , B = 70, C = 70] If comparing A and B, then debtValue will be 70 = min(abs(-140), 70).
                        BigDecimal absLentAmount = lentAmount.abs();
                        BigDecimal debtValue = absLentAmount.min(borrowedAmount);
                        borrowPerson.addIndividualBalance(lenderPerson, debtValue);
                        
                        BigDecimal convertedValue = debtValue.divide(currencyRate, 3, RoundingMode.HALF_UP); 
                        borrowPerson.addBalanceConverted(lenderPerson, convertedValue);

                        // Settling the the debt this round. 
                        // If borrower still has remaining debt(positive value) then the for loop will continue to the next person with a negative value
                        borrowedAmount.subtract(debtValue); 
    
                        // Update the expenseBalance for lenderPerson to reflect settled debt
                        expenseBalance.put(lenderPerson, lentAmount.add(debtValue));
                    }
                }
                // Update expenseBalance for borrowPerson with the remaining balance after settling debts
                expenseBalance.put(borrowPerson, borrowedAmount);
            }
        }
        calculateExpense(); // Reset the credit HashMap back to original values for integrity/other methods after calculating exact debts
    }

    //attempt#2 - moved this to the calculateCredit
    // public void calculateCurrencyConversion(){
    //     expenseBalance.forEach((person, value) -> {
    //         double newValue = value / rate;
    //         newValue = Double.parseDouble(String.format("%.2f", newValue));
    //         expenseBalanceConverted.put(person, newValue);
    //     });
    // }


    /* attempt #1 - DONT LIKE IT DUE TO NEEDING TO USE IT EXTERNALLY BECAUSE OF THE PARAMETER
    public void calculateCurrencyConversion(ExchangeRate rate){
        expenseBalance.forEach((person, value) -> {
            double newValue = rate.convertToUSD(value);
            expenseBalanceConverted.put(person, newValue);
        });
    }
    */
    
    //old toString
    /* 
    public String toString(){
        String s = "Expense title: " + title + "\tDate: " + date + "\t Amount: " + amount;
        s += "\n ------------------Details------------------";
        s += "\n~~~Paid by~~~";
        for (Map.Entry<Person, Double> pair : paidBy.entrySet()) {
            s += "\n" + pair.getKey().getName() + " : " + pair.getValue();
        }
        
        s += "\n\n~~~Responsible for~~~ ";
        for (Map.Entry<Person, Double> pair : splits.entrySet()) {
            s += "\n" + pair.getKey().getName() + " : " + pair.getValue();
        }

        return s;

    }
    */

    /*
    public String toString(){
        String s = "ID: " + id + "  | Title: " + title + " | Date(y/m/d): " + date + " | Amount: " + amount;
        //s += "\n ------------------Details------------------";
        s += "\nPaid by: " + paidBy;
        s += "\nSplit responsible: " + splits;
        s += "\nexpenseBalance/Debts: " + expenseBalance;
        return s;
    }
        */
    
    //New toString to not print key:value with 0.0 because we now initialize the Map fields with 0.0 as default when adding participants. 
    public String toString(){
        String s = "ID: " + id + "  | Title: " + title + " | Date: " + date + " | Amount: " + amount + " | Rate: " + currencyRate;
        
        //Payers
        s += "\nPaid by: {";
        String data = "";
        for (Map.Entry<Person, BigDecimal> pair : paidBy.entrySet()) {
            if (pair.getValue().compareTo(BigDecimal.ZERO) != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";
        
        //Splits
        s += "\nSplits: {";
        data = "";
        for (Map.Entry<Person, BigDecimal> pair : splits.entrySet()) {
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