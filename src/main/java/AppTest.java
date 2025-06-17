import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class AppTest {
    public static void main(String[] args) throws Exception {
        
        //Implementing Trip Class
        String tripName = "Japan 2025";
        Trip trip = new Trip(tripName);

        trip.addPerson("Henry");
        trip.addPerson("Van");
        trip.addPerson("Khoa");

        List<Person> participants = trip.getParticipantsList(); //getting participants from trip class right away 

        //~~~~~~~~~~~~~~~~Expense1 set up - Build outside in the driver class
        Expense expense1 = new Expense();
        expense1.setParticipants(participants);
        expense1.setTitle("Yakiniku Like");
        expense1.setAmount(new BigDecimal("6000.00"));
        expense1.setDate(LocalDate.now().toString());
        

        expense1.setPayer(participants.get(0), new BigDecimal("2000.00"));  // henry paid 500
        expense1.setPayer(participants.get(2), new BigDecimal("4000.00")); // khoa paid 2500

        expense1.evenSplit(); // even split
        expense1.setCurrencyRate(new BigDecimal("148.7")); //setting rate


        trip.addExpense(expense1);
        expense1.calculateExpense();
        expense1.calculateExactDebt(); 
        
        //~~~~~~~~~~~~~~~~Expense2 set up - manual splits
        Expense expense2 = new Expense();
        expense2.setParticipants(participants);  
        expense2.setTitle("Comodi iida");
        expense2.setAmount(new BigDecimal("3000.00"));
        expense2.setDate(LocalDate.now().toString());

        expense2.setPayer(participants.get(1), new BigDecimal("3000.00")); // van paid 3000

        expense2.setSplit(participants.get(0), new BigDecimal("1000.00")); // henry's split
        expense2.setSplit(participants.get(1), new BigDecimal("1000.00")); // van's split
        expense2.setSplit(participants.get(2), new BigDecimal("1000.00")); // khoa's split
        expense2.setCurrencyRate(new BigDecimal("148.7")); //setting rate
        
        trip.addExpense(expense2);
        expense2.calculateExpense();
        expense2.calculateExactDebt();

        //~~~~~~~~~~~~~~~~Expense3 set up - testing exchange rate fetcher
        Expense expense3 = new Expense();
        expense3.setParticipants(participants);
        expense3.setTitle("Uniqlo");
        expense3.setAmount(new BigDecimal("10000.00"));
        expense3.setDate(LocalDate.now().toString());
        
        expense3.setPayer(participants.get(2), new BigDecimal("10000.00")); // khoa paid 10000
        
        expense3.setSplit(participants.get(0), new BigDecimal("5000.00")); // henry's split
        expense3.setSplit(participants.get(1), new BigDecimal("5000.00")); // van's split
    
        BigDecimal rate = ExchangeRateFetcher.getRate("USD", "JPY", LocalDate.now().toString()); // Fetching the exchange rate
        expense3.setCurrencyRate(rate); 

        trip.addExpense(expense3);
        expense3.calculateExpense();
        expense3.calculateExactDebt();


        // Print out the expenses
        System.out.println("\n~~~~~Printing expenses List: ~~~~~~");
        for (Expense expense : trip.getExpenseList()) {
            System.out.println(expense);
            System.out.println("USD value: "  + expense.getExpenseBalanceConverted());
            System.out.println();
        }
        // Print out the participants and their exact debts
        System.out.println("\n~~~~~~Printing participants' exact debt~~~~~~");
        for (Person participant : trip.getParticipantsList()) {
            System.out.println(participant + " owes: YEN " + participant.getExactDebt() + " \t\tUSD " + participant.getExactDebtConverted());
        }
        // Print out the total balance of all expenses
        System.out.println("\n~~~~~~Printing total balance of all expenses/simplified debt~~~~~~");
        Map<Person, BigDecimal> totalBalance = trip.calculateTotalExpense();
        System.out.println("YEN " + totalBalance);
        Map<Person, BigDecimal> totalBalanceConverted = trip.calculateTotalExpenseConverted();
        System.out.println("USD " + totalBalanceConverted);

        // // OLD DRIVER CODE BELOW
        // List<Expense> expenses = new ArrayList<>();

        // Person henry = new Person("Henry"); participants.add(henry);
        // Person van = new Person("Van"); participants.add(van);
        // Person khoa = new Person("Khoa"); participants.add(khoa);

        // //Expense #1 - Manually setting payers and setting splits
        // String title = "Cocoichiban2";
        // BigDecimal amount = new BigDecimal("3000.00");
        // String date = LocalDate.now().toString();
        // Expense e1 = new Expense(title, amount, date);
        // expenses.add(e1);
        // e1.setParticipants(participants);

        // e1.setPayer(henry, new BigDecimal("500"));
        // e1.setPayer(khoa, new BigDecimal("2500"));

        //     //Manually setting split responsibilities one at the time
        // e1.setSplit(henry, new BigDecimal("1000")); 
        // e1.setSplit(van, new BigDecimal("1000"));
        // e1.setSplit(khoa, new BigDecimal("1000"));

        // e1.setRate(new BigDecimal("148.7")); 

        // e1.calculateCredits();
        // e1.calculateExactDebt();  
        // System.out.println("\n\n" + e1);

        // System.out.println("USD value: " + e1.getConvertedCreditsMap());

        
        // //Expense #2 - Testing evenSplit methods from Expense class
        // String date2 = "2024-10-02";
        // Expense e2 = new Expense("7-11", new BigDecimal("6000"), date2);
        // expenses.add(e2);
        // e2.setParticipants(participants);
        
        // e2.setPayer(van, new BigDecimal("6000"));

        // e2.evenSplit();

        // e2.setRate(new BigDecimal("155"));
        
        // e2.calculateCredits();
        // e2.calculateExactDebt();
        // System.out.println("\n\n" + e2);
        // System.out.println("USD value: "  + e2.getConvertedCreditsMap());

         
        // //Expense #3
        // String strDate = "2024-10-04";

        // Expense e3 = new Expense("Snacks", new BigDecimal("2000"), strDate);
        // expenses.add(e3);
        // e3.setParticipants(participants);

        // e3.setPayer(van, new BigDecimal("2000"));

        // e3.setSplit(van, new BigDecimal("1000"));
        // e3.setSplit(henry, new BigDecimal("1000"));

        // e3.setRate(new BigDecimal("145"));

        // e3.calculateCredits();
        // System.out.println("\n\n" + e3);
        // e3.calculateExactDebt();
        // System.out.println("USD value: "  + e3.getConvertedCreditsMap());

        // //Testing calculateExactSplit from Expense class
        // System.out.println("\n\nTesting calculateExactDebt in USD: ");
        // System.out.println(henry + "'s exact debt: " + henry.getExactDebt());
        // System.out.println(van + "'s exact debt: " + van.getExactDebt());
        // System.out.println(khoa + "'s exact debt: " + khoa.getExactDebt());
        
        // //Testing sumCredits 
        // System.out.println("\n\nTesting sumCredits to print simplified debt in USD: ");
        // System.out.println(sumCredits(expenses));

        // //TESTING JSON STUFF
        // ObjectMapper mapper = new ObjectMapper();
        // mapper.writeValue(new File("expense.json"), expenses);
    }

    /*
    This method accepts a list of Expense objects and return a HashMap of person, value pair simplifying all the debts across all expenses. 
    */
    public static Map<Person, BigDecimal> sumExpenseBalance (List<Expense> expenses){
        Map<Person, BigDecimal> total = new HashMap<>();

        for (Expense expense : expenses) {
            expense.getExpenseBalanceConverted().forEach((person, value) -> total.merge(person, value, BigDecimal::add));
        }
        return total;
    }

    /*
    //Accessing the Map with get(date) method to get the object value and using the object's getter for the rate
    public static double accessingRateMap(LocalDate date, Map<LocalDate, ExchangeRate> map){
        return map.get(date).getRate();
    }
    */

}
