import java.util.*;

public class Expense {
    private static int counter;
    private int id;
    private String title;
    private double amount;
    private boolean payBack = false;
    private String currencyType;  
    private double rate;
    private String date; 
    private List<Person> participants; 
    private Map<Person, Double> paidBy = new HashMap<>();  //keep track of who paid the expense
    private Map<Person, Double> splits = new HashMap<>(); //keep track of who's responsible for x amount
    private Map<Person, Double> credits = new HashMap<>(); //paidby minus splits
    private Map<Person, Double> creditsConverted = new HashMap<>();

    public Expense(){}
    public Expense(String title, double amount, String date){
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
    public void setAmount(double amount){
        this.amount = amount;
    }

    public double getAmount(){
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
    public void setRate(double rate){
        this.rate = rate;
    }

    public double getRate(){
        return rate;
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

        for (Person person : participants) {
            paidBy.put(person, 0.0);
            splits.put(person, 0.0);
        }
    }

    public List<Person> getparticipants(){
        return participants;
    }
    
    /*
    Setter method to set payer(s) with amount paid for this expense.
    Parameters:  the payer and the amount paid
    */
    public void setPayer(Person payer, double paidAmount){
        paidBy.put(payer, paidAmount);
    }


    public Map<Person, Double> getPayerMap(){
        return paidBy;
    }
    
    /*
    Setter method to set a person and their split of the bill that they're responsible for.
    Parameters: the person and their split amount
    */
    public void setSplit(Person person, double splitAmount){
        splits.put(person, splitAmount);
    }

    public Map<Person, Double> getSplitsMap(){
        return splits;
    }

    /*
    Split the bill amount evenly with the size of participants array. Then the for loop will iterate through 
    participants array and using the setSplit method to set the person object and the amount responsible. 
    */
    public void evenSplit(){
        double even = amount / participants.size();
        for (int i = 0; i < participants.size(); i++){
            //splits.put(participants.get(i), even);
            setSplit(participants.get(i), even);
        }
    }

    //TODO?
    public void exactSplit(Person person, double amount){
        //give this method the person and the amount responsible
        //hmmm....maybe this shouldn't have parameters but when called, it'll just loop through the array of participants
        //asks for the exact amount and set the splits array manually. probably should have some conditions to prevent error inputs
    }

    /*
    Apparently HashMap got a forEach method! But all the logic has to be done within the method parenthesis (). 
    splits.forEach(   (key, value) -> {some logic statements}   );  a lot of spaces for the outside most () to visualize this method syntax.
    Only need to calculate the credit/difference for participants who are responsible for the expense so we itterate through the splits HashMap. 
    For now, since we aren't creating an entry for the participants who didn't paid/paid zero in the paidBy HashMap, we need to check if the participants
    in splits HashMap are also in the paidBy HashMap before doing the necessary calculation. If the responsible/split person is NOT in the paidBy
    HashMap (they didn't paid the bill), that amount that they're responsible for will carry over to the credits HashMap.
    */
    public void calculateCredits(){
        splits.forEach((key, value) -> {
            if(paidBy.containsKey(key)){
                double paidAmount = paidBy.get(key);
                double splitAmount = value;
                double diffAmount = splitAmount - paidAmount;

                //foreign currency
                credits.put(key, diffAmount);

                //converted currency
                double newValue = diffAmount / rate;
                newValue = Double.parseDouble(String.format("%.2f", newValue));
                creditsConverted.put(key, newValue);

            } else if(!paidBy.containsKey(key)){
                //foreign currency
                credits.put(key, value);

                //converted currency
                double newValue = value / rate;
                newValue = Double.parseDouble(String.format("%.2f", newValue));
                creditsConverted.put(key, newValue);
            }
        });      
    }

    //Getter for the credits HashMap
    public Map<Person, Double> getCreditsMap(){
        return credits;
    }

    /*
    Calculates the exact debt using the credits HashMap like {A=-140, B=70, C=70}.
    Positive credit means that you borrowed that amount (like a credit card). 
    Negative credit means that someone owes you (the lender) money and the value is the amount you've lent.
    In the given example above, A lent 140 while B and C both borrowed 70 each. 
    Each Person object has HashMap<person, value> field called exactDebt and this method will add the lender and amount owed.
    For instance, this method will add {A=70} to person B and C exactDebt Maps since they both owe A an amount of 70.

    This method has an outside for loop that iterates to find the borrower then the inside for loop that iterates to find the lender.
    In all cases, the amount that needs to be settled is always the smaller value between the lender and borrower values. That smallest value 
    "debtValue" and lender object will be added to the exactDebt Map in the respective borrower Person object. 
    */
    public void calculateExactDebt() {
        for (Map.Entry<Person, Double> entryA : credits.entrySet()) {
            Person personA = entryA.getKey();
            double valueA = entryA.getValue();
            
            if (valueA > 0) { //Find the borrower 
                double positiveA = valueA;
    
                for (Map.Entry<Person, Double> entryB : credits.entrySet()) {
                    Person personB = entryB.getKey();
                    double valueB = entryB.getValue();
    
                    if (positiveA <= 0) break; //Exit when the borrower's balance is settled
    
                    if (valueB < 0) { //Find the lender
                        double negativeB = Math.abs(valueB); //Need abs value to make comparison
                        double debtValue = (negativeB > positiveA) ? positiveA : negativeB; //Debt value can't be the largest of the two. Has to be >=
                        
                        //////////////////////////////////////////
                        //Converting the debtValue to USD(or whatever currency rate) to add to the person's debt record
                        //Disable the three statements below and enable the statement after these three if you want original value
                        double convertedValue = debtValue / rate;
                        convertedValue = Double.parseDouble(String.format("%.2f", convertedValue));
                        personA.addExactDebt(personB, convertedValue);
                        //////////////////////////////////////////

                        //personA.addExactDebt(personB, debtValue); //Add debt record - foreign currency. Reenable this line if you want original value
                        positiveA -= debtValue; //Settling the the debt for lender or borrower. If borrower still has remaining debt(positive value) then the for loop will continue to the next person with a negative value
    
                        //Update the credits for personB to reflect settled debt
                        credits.put(personB, valueB + debtValue);
                    }
                }
                //Update credits for personA with the remaining balance after settling debts
                credits.put(personA, positiveA);
            }
        }
        calculateCredits(); //Reset the different HashMap back to normal for other calculations
    }

    //attempt#2 - moved this to the calculateCredit
    public void calculateCurrencyConversion(){
        credits.forEach((person, value) -> {
            double newValue = value / rate;
            newValue = Double.parseDouble(String.format("%.2f", newValue));
            creditsConverted.put(person, newValue);
        });
    }


    /* attempt #1 - DONT LIKE IT DUE TO NEEDING TO USE IT EXTERNALLY BECAUSE OF THE PARAMETER
    public void calculateCurrencyConversion(ExchangeRate rate){
        credits.forEach((person, value) -> {
            double newValue = rate.convertToUSD(value);
            creditsConverted.put(person, newValue);
        });
    }
    */

    public Map<Person, Double> getConvertedCreditsMap(){
        return creditsConverted;
    }
    
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
        s += "\nCredits/Debts: " + credits;
        return s;
    }
        */
    
    //New toString to not print key:value with 0.0 because we now initialize the Map fields with 0.0 as default when adding participants. 
    public String toString(){
        String s = "ID: " + id + "  | Title: " + title + " | Date(y/m/d): " + date + " | Amount: " + amount;
        
        //Payers
        s += "\nPaid by: {";
        String data = "";
        for (Map.Entry<Person, Double> pair : paidBy.entrySet()) {
            if (pair.getValue() != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";
        
        //Splits
        s += "\nSplits: {";
        data = "";
        for (Map.Entry<Person, Double> pair : splits.entrySet()) {
            if (pair.getValue() != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";
        
        //Credits
        s += "\nCredits: {";
        data = "";
        for (Map.Entry<Person, Double> pair : credits.entrySet()) {
            if (pair.getValue() != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";

        return s;
    }
        
}