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
    private BigDecimal rate;
    private String date; 
    private List<Person> participants; 
    private Map<Person, BigDecimal> paidBy = new HashMap<>();  //keep track of who paid the expense
    private Map<Person, BigDecimal> splits = new HashMap<>(); //keep track of who's responsible for x amount
    private Map<Person, BigDecimal> credits = new HashMap<>(); //paidby minus splits
    private Map<Person, BigDecimal> creditsConverted = new HashMap<>(); //converted to other currency

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
    public void setRate(BigDecimal rate){
        this.rate = rate;
    }

    public BigDecimal getRate(){
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
        paidBy.put(payer, paidAmount);
    }


    public Map<Person, BigDecimal> getPayerMap(){
        return paidBy;
    }
    
    /*
    Setter method to set a person and their split of the bill that they're responsible for.
    Parameters: the person and their split amount
    */
    public void setSplit(Person person, BigDecimal splitAmount){
        splits.put(person, splitAmount);
    }

    public Map<Person, BigDecimal> getSplitsMap(){
        return splits;
    }

    /*
    Split the bill amount evenly between all participants. The for loop will iterate through 
    participants array and use the setSplit method. 
    */
    public void evenSplit(){
        BigDecimal even = amount.divide(
            BigDecimal.valueOf(participants.size()),
      2, // scale: number of decimal places
            RoundingMode.HALF_UP // rounding mode
        );

        for (int i = 0; i < participants.size(); i++){
            //splits.put(participants.get(i), even); // using the Map's put method directly
            setSplit(participants.get(i), even); // using custom setter method
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
    splits.forEach(   (key, value) -> {some logic statements}   );  //extra space is for readability

    Only need to calculate the credit/difference for participants who are responsible for the expense so we itterate through the splits HashMap. 
    For now, since we aren't creating an entry for the participants who didn't paid/paid zero in the paidBy HashMap, we need to check if the participants
    in splits HashMap are also in the paidBy HashMap before doing the necessary calculation. If the responsible/split person is NOT in the paidBy
    HashMap (they didn't paid the bill), that amount that they're responsible for will carry over to the credits HashMap.
    */

    // Calculates the credits for each participant based on the paidBy and splits HashMaps in this class.
    // Updates the credits and creditsConverted HashMaps.
    public void calculateCredits() {
        for (Person person : participants) {
            BigDecimal paidAmount = paidBy.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal splitAmount = splits.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal diffAmount = splitAmount.subtract(paidAmount);
    
            credits.put(person, diffAmount);
            BigDecimal newValue = diffAmount.divide(rate, 2, RoundingMode.HALF_UP);
            creditsConverted.put(person, newValue);
        }
    }
    //Getter for the credits HashMap
    public Map<Person, BigDecimal> getCreditsMap(){
        return credits;
    }

    // This map contains the converted credits for each participant based on the exchange rate.
    public Map<Person, BigDecimal> getConvertedCreditsMap(){
        return creditsConverted;
    }

    /*
    Calculates the exact debt using the credits HashMap like {A=-140, B=70, C=70}.
    Positive credit means that you borrowed that amount (like a credit card). 
    Negative credit means that someone owes you money and the value is the amount you (the lender) have lent.
    In the given example above, A lent 140 while B and C both borrowed 70 each. 
    Each Person object has HashMap<person, value> field called 'exactDebt' and this method will update exactDebt with {lender:amount}.
    For instance, this method will add {A:70} to person B and C 'exactDebt' Maps since they both owe A an amount of 70.

    This method has an outside for-loop that iterates through 'credits' Map to find the borrower (positive value) then the inside for-loop that iterates to find the lender (negative value).
    In all cases, the amount that needs to be settled is always the smaller value between the two values (abs(lender) and borrower). 
    This smallest value between the two will then is used to update borrower's exactDebt and update the lender's credits Map. 
    */

    // Calculates the exact debt for each borrower based on the credits HashMap ONLY. No other Map is used in this method.
    // This method assumes that the credits HashMap has been calculated before calling this method. credit Map will be updated along the way and will be reset to original values after this method is done
    // This method will update the borrower's Person class exactDebt Map with the lender and the amount owed - {lender:amount}.
    public void calculateExactDebt() {
        // For-loop to find the borrower (positive value)
        for (Map.Entry<Person, BigDecimal> entry : credits.entrySet()) {
            Person borrowPerson = entry.getKey();
            BigDecimal borrowedAmount = entry.getValue();
            
            if (borrowedAmount.compareTo(BigDecimal.ZERO) > 0) { // Find the borrower if positive value
                // For-loop to find the lender (negative value)
                for (Map.Entry<Person, BigDecimal> entryB : credits.entrySet()) {
                    Person lenderPerson = entryB.getKey();
                    BigDecimal lentAmount = entryB.getValue();
    
                    if (borrowedAmount.compareTo(BigDecimal.ZERO) <= 0) break; // Exit this loop when the borrower's balance is settled (non positive)
    
                    if (lentAmount.compareTo(BigDecimal.ZERO) < 0) { // Find the lender, negative value
                        // Get the smaller value between the lender and borrower. Need to abs value of the lender since it's negative.
                        // Ex. [A = -140 , B = 70, C = 70] If comparing A and B, then debtValue will be 70 = min(abs(-140), 70).
                        BigDecimal absLentAmount = lentAmount.abs();
                        BigDecimal debtValue = absLentAmount.min(borrowedAmount);
                        
                        /////////// OPTION A: CONVERTED VALUE ///////////
                        // Converting the debtValue to USD(or whatever currency rate) to add to the person's debt record
                        // Disable the three statements below and enable the statement after these three if you want original value
                        BigDecimal convertedValue = debtValue.divide(rate, 2, RoundingMode.HALF_UP); 
                        borrowPerson.addExactDebt(lenderPerson, convertedValue); // adding {lender:amount owed} to borrowPerson's exactDebt Map
                        ////////////////////////////////////////////////

                        /////////// OPTION B: ORIGINAL VALUE ///////////
                        // Adding {lender:amount owed} to borrowPerson's exactDebt Map. Reenable this line if you want original value
                        //borrowPerson.addExactDebt(lenderPerson, debtValue); //REENABLE THIS LINE IF YOU WANT ORIGINAL VALUE
                        ////////////////////////////////////////////////

                        // Settling the the debt this round. 
                        // If borrower still has remaining debt(positive value) then the for loop will continue to the next person with a negative value
                        borrowedAmount.subtract(debtValue); 
    
                        // Update the credits for lenderPerson to reflect settled debt
                        credits.put(lenderPerson, lentAmount.add(debtValue));
                    }
                }
                // Update credits for borrowPerson with the remaining balance after settling debts
                credits.put(borrowPerson, borrowedAmount);
            }
        }
        calculateCredits(); // Reset the credit HashMap back to original values for integrity/other methods after calculating exact debts
    }

    //attempt#2 - moved this to the calculateCredit
    // public void calculateCurrencyConversion(){
    //     credits.forEach((person, value) -> {
    //         double newValue = value / rate;
    //         newValue = Double.parseDouble(String.format("%.2f", newValue));
    //         creditsConverted.put(person, newValue);
    //     });
    // }


    /* attempt #1 - DONT LIKE IT DUE TO NEEDING TO USE IT EXTERNALLY BECAUSE OF THE PARAMETER
    public void calculateCurrencyConversion(ExchangeRate rate){
        credits.forEach((person, value) -> {
            double newValue = rate.convertToUSD(value);
            creditsConverted.put(person, newValue);
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
        
        //Credits
        s += "\nCredits: {";
        data = "";
        for (Map.Entry<Person, BigDecimal> pair : credits.entrySet()) {
            if (pair.getValue().compareTo(BigDecimal.ZERO) != 0.0) data += " " + pair.getKey() + "=" + pair.getValue() + ",";
        }
        s += data.trim().replaceAll(",$", "") + "}";

        return s;
    }
        
}