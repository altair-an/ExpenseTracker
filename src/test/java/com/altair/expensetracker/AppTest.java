// package com.altair.expensetracker;
// import java.io.File;
// import java.math.BigDecimal;
// import java.time.LocalDate;

// import com.altair.expensetracker.entity.Expense;
// import com.altair.expensetracker.entity.Person;
// import com.altair.expensetracker.entity.Trip;
// import com.altair.expensetracker.util.ExchangeRateFetcher;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.util.*;
// import org.junit.jupiter.api.Test;

// public class AppTest {
//     @Test
//     public static void main(String[] args) throws Exception {
        
//         //Implementing Trip Class
//         String tripName = "Japan 2025";
//         Trip trip = new Trip(tripName);

//         trip.addPerson("Henry");
//         trip.addPerson("Van");
//         trip.addPerson("Khoa");

//         List<Person> participants = trip.getParticipantsList(); //getting participants from trip class right away 

//         //~~~~~~~~~~~~~~~~Expense1 set up - Build outside in the driver class
//         Expense expense1 = new Expense();
//         expense1.setParticipants(participants);
//         expense1.setTitle("Yakiniku Like");
//         expense1.setAmount(new BigDecimal("6000.00"));
//         expense1.setDate(LocalDate.now().toString());
        

//         expense1.setPayer(participants.get(0), new BigDecimal("2000.00"));  // henry paid 500
//         expense1.setPayer(participants.get(2), new BigDecimal("4000.00")); // khoa paid 2500

//         expense1.evenSplit(); // even split
//         expense1.setCurrencyRate(new BigDecimal("148.7")); //setting rate


//         trip.addExpense(expense1);
//         expense1.calculateExpense();
//         expense1.calculateIndividualBalances(); 
        
//         //~~~~~~~~~~~~~~~~Expense2 set up - manual splits
//         Expense expense2 = new Expense();
//         expense2.setParticipants(participants);  
//         expense2.setTitle("Comodi iida");
//         expense2.setAmount(new BigDecimal("3000.00"));
//         expense2.setDate(LocalDate.now().toString());

//         expense2.setPayer(participants.get(1), new BigDecimal("3000.00")); // van paid 3000

//         expense2.setSplit(participants.get(0), new BigDecimal("1000.00")); // henry's split
//         expense2.setSplit(participants.get(1), new BigDecimal("1000.00")); // van's split
//         expense2.setSplit(participants.get(2), new BigDecimal("1000.00")); // khoa's split
//         expense2.setCurrencyRate(new BigDecimal("148.7")); //setting rate
        
//         trip.addExpense(expense2);
//         expense2.calculateExpense();
//         expense2.calculateIndividualBalances();

//         //~~~~~~~~~~~~~~~~Expense3 set up - testing exchange rate fetcher
//         Expense expense3 = new Expense();
//         expense3.setParticipants(participants);
//         expense3.setTitle("Uniqlo");
//         expense3.setAmount(new BigDecimal("10000.00"));
//         expense3.setDate(LocalDate.now().toString());
        
//         expense3.setPayer(participants.get(2), new BigDecimal("10000.00")); // khoa paid 10000
        
//         expense3.setSplit(participants.get(0), new BigDecimal("5000.00")); // henry's split
//         expense3.setSplit(participants.get(1), new BigDecimal("5000.00")); // van's split
    
//         BigDecimal rate = ExchangeRateFetcher.getRate("USD", "JPY", LocalDate.now().toString()); // Fetching the exchange rate
//         expense3.setCurrencyRate(rate); 

//         trip.addExpense(expense3);
//         expense3.calculateExpense();
//         expense3.calculateIndividualBalances();


//         // Print out the expenses
//         System.out.println("\n~~~~~Printing expenses List: ~~~~~~");
//         for (Expense expense : trip.getExpenseList()) {
//             System.out.println(expense);
//             System.out.println("USD value: "  + expense.getExpenseBalanceConverted());
//             System.out.println();
//         }
//         // Print out the participants and their exact debts
//         System.out.println("\n~~~~~~Printing participants' exact debt in YEN and USD~~~~~~");
//         for (Person participant : trip.getParticipantsList()) {
//             // Printing YEN and USD balances in cleanly aligned format
//             System.out.printf("%s owes: %-30s \t USD%s %n", 
//                               participant, 
//                               participant.getIndividualBalance(), 
//                               participant.getBalanceConverted());
//         }
//         // Print out the total balance of all expenses
//         // System.out.println("\n~~~~~~Printing total balance of all expenses/simplified debt~~~~~~");
//         // Map<Person, BigDecimal> totalBalance = trip.calculateTotalExpense();
//         // System.out.println("YEN " + totalBalance);
//         // Map<Person, BigDecimal> totalBalanceConverted = trip.calculateTotalExpenseConverted();
//         // System.out.println("USD " + totalBalanceConverted);
//         // System.out.println();

//         // Testing to see if payers and splits are correctly set
//         System.out.println("~~~~~~Printing payers and splits for each expense~~~~~~");
//         for (Expense expense : trip.getExpenseList()) {
//             System.out.println("Expense Title: " + expense.getTitle());
//             System.out.println("Payers List: " + expense.getPayerList());
//             System.out.println("Splits List: " + expense.getSplitsList());
//             System.out.println();
//         }





//         // //TESTING JSON STUFF
//         //ObjectMapper mapper = new ObjectMapper();
//         //mapper.writeValue(new File("expense.json"), trip);
//     }

//     @Test
//     void testSomething() throws Exception {
//         System.out.println("This test runs!");
//         //Implementing Trip Class
//         String tripName = "Japan 2025";
//         Trip trip = new Trip(tripName);

//         trip.addPerson("Henry");
//         trip.addPerson("Van");
//         trip.addPerson("Khoa");

//         List<Person> participants = trip.getParticipantsList(); //getting participants from trip class right away 

//         //~~~~~~~~~~~~~~~~Expense1 set up - Build outside in the driver class
//         Expense expense1 = new Expense();
//         expense1.setParticipants(participants);
//         expense1.setTitle("Yakiniku Like");
//         expense1.setAmount(new BigDecimal("6000.00"));
//         expense1.setDate(LocalDate.now().toString());
        

//         expense1.setPayer(participants.get(0), new BigDecimal("2000.00"));  // henry paid 500
//         expense1.setPayer(participants.get(2), new BigDecimal("4000.00")); // khoa paid 2500

//         expense1.evenSplit(); // even split
//         expense1.setCurrencyRate(new BigDecimal("148.7")); //setting rate


//         trip.addExpense(expense1);
//         expense1.calculateExpense();
//         expense1.calculateIndividualBalances(); 
        
//         //~~~~~~~~~~~~~~~~Expense2 set up - manual splits
//         Expense expense2 = new Expense();
//         expense2.setParticipants(participants);  
//         expense2.setTitle("Comodi iida");
//         expense2.setAmount(new BigDecimal("3000.00"));
//         expense2.setDate(LocalDate.now().toString());

//         expense2.setPayer(participants.get(1), new BigDecimal("3000.00")); // van paid 3000

//         expense2.setSplit(participants.get(0), new BigDecimal("1000.00")); // henry's split
//         expense2.setSplit(participants.get(1), new BigDecimal("1000.00")); // van's split
//         expense2.setSplit(participants.get(2), new BigDecimal("1000.00")); // khoa's split
//         expense2.setCurrencyRate(new BigDecimal("148.7")); //setting rate
        
//         trip.addExpense(expense2);
//         expense2.calculateExpense();
//         expense2.calculateIndividualBalances();

//         //~~~~~~~~~~~~~~~~Expense3 set up - testing exchange rate fetcher
//         Expense expense3 = new Expense();
//         expense3.setParticipants(participants);
//         expense3.setTitle("Uniqlo");
//         expense3.setAmount(new BigDecimal("10000.00"));
//         expense3.setDate(LocalDate.now().toString());
        
//         expense3.setPayer(participants.get(2), new BigDecimal("10000.00")); // khoa paid 10000
        
//         expense3.setSplit(participants.get(0), new BigDecimal("5000.00")); // henry's split
//         expense3.setSplit(participants.get(1), new BigDecimal("5000.00")); // van's split
    
//         BigDecimal rate = ExchangeRateFetcher.getRate("USD", "JPY", LocalDate.now().toString()); // Fetching the exchange rate
//         expense3.setCurrencyRate(rate); 

//         trip.addExpense(expense3);
//         expense3.calculateExpense();
//         expense3.calculateIndividualBalances();


//         // Print out the expenses
//         System.out.println("\n~~~~~Printing expenses List: ~~~~~~");
//         for (Expense expense : trip.getExpenseList()) {
//             System.out.println(expense);
//             System.out.println("USD value: "  + expense.getExpenseBalanceConverted());
//             System.out.println();
//         }
//         // Print out the participants and their exact debts
//         System.out.println("\n~~~~~~Printing participants' exact debt in YEN and USD~~~~~~");
//         for (Person participant : trip.getParticipantsList()) {
//             // Printing YEN and USD balances in cleanly aligned format
//             System.out.printf("%s owes: %-30s \t USD%s %n", 
//                               participant, 
//                               participant.getIndividualBalance(), 
//                               participant.getBalanceConverted());
//         }
//         // Print out the total balance of all expenses
//         // System.out.println("\n~~~~~~Printing total balance of all expenses/simplified debt~~~~~~");
//         // Map<Person, BigDecimal> totalBalance = trip.calculateTotalExpense();
//         // System.out.println("YEN " + totalBalance);
//         // Map<Person, BigDecimal> totalBalanceConverted = trip.calculateTotalExpense();
//         // System.out.println("USD " + totalBalanceConverted);
//         // System.out.println();

//         // Testing to see if payers and splits are correctly set
//         System.out.println("~~~~~~Printing payers and splits for each expense~~~~~~");
//         for (Expense expense : trip.getExpenseList()) {
//             System.out.println("Expense Title: " + expense.getTitle());
//             System.out.println("Payers List: " + expense.getPayerList());
//             System.out.println("Splits List: " + expense.getSplitsList());
//             System.out.println();
//         }





//         // //TESTING JSON STUFF
//         ObjectMapper mapper = new ObjectMapper();
//         mapper.writeValue(new File("expenseTest.json"), trip);
//     }

// }
