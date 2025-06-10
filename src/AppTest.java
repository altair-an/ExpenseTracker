import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class AppTest {
    public static void main(String[] args) throws IOException {

        List<Person> participants = new ArrayList<>();
        List<Expense> expenses = new ArrayList<>();

        Person henry = new Person("Henry"); participants.add(henry);
        Person van = new Person("Van"); participants.add(van);
        Person khoa = new Person("Khoa"); participants.add(khoa);

        //Expense #1 - Manually setting payers and setting splits
        String title = "Cocoichiban2";
        BigDecimal amount = new BigDecimal("3000.00");
        String date = LocalDate.now().toString();
        Expense e1 = new Expense(title, amount, date);
        expenses.add(e1);
        e1.setParticipants(participants);

        e1.setPayer(henry, new BigDecimal("500"));
        e1.setPayer(khoa, new BigDecimal("2500"));

            //Manually setting split responsibilities one at the time
        e1.setSplit(henry, new BigDecimal("1000")); 
        e1.setSplit(van, new BigDecimal("1000"));
        e1.setSplit(khoa, new BigDecimal("1000"));

        e1.setRate(new BigDecimal("148.7")); 

        e1.calculateCredits();
        e1.calculateExactDebt();  
        System.out.println("\n\n" + e1);

        System.out.println("USD value: " + e1.getConvertedCreditsMap());

        
        //Expense #2 - Testing evenSplit methods from Expense class
        String date2 = "2024-10-02";
        Expense e2 = new Expense("7-11", new BigDecimal("6000"), date2);
        expenses.add(e2);
        e2.setParticipants(participants);
        
        e2.setPayer(van, new BigDecimal("6000"));

        e2.evenSplit();

        e2.setRate(new BigDecimal("155"));
        
        e2.calculateCredits();
        e2.calculateExactDebt();
        System.out.println("\n\n" + e2);
        System.out.println("USD value: "  + e2.getConvertedCreditsMap());

         
        //Expense #3
        String strDate = "2024-10-04";

        Expense e3 = new Expense("Snacks", new BigDecimal("2000"), strDate);
        expenses.add(e3);
        e3.setParticipants(participants);

        e3.setPayer(van, new BigDecimal("2000"));

        e3.setSplit(van, new BigDecimal("1000"));
        e3.setSplit(henry, new BigDecimal("1000"));

        e3.setRate(new BigDecimal("145"));

        e3.calculateCredits();
        System.out.println("\n\n" + e3);
        e3.calculateExactDebt();
        System.out.println("USD value: "  + e3.getConvertedCreditsMap());

        //Testing calculateExactSplit from Expense class
        System.out.println("\n\nTesting calculateExactDebt in USD: ");
        System.out.println(henry + "'s exact debt: " + henry.getExactDebt());
        System.out.println(van + "'s exact debt: " + van.getExactDebt());
        System.out.println(khoa + "'s exact debt: " + khoa.getExactDebt());
        
        //Testing sumCredits 
        System.out.println("\n\nTesting sumCredits to print simplified debt in USD: ");
        System.out.println(sumCredits(expenses));

        //TESTING JSON STUFF
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("expense.json"), expenses);
    }

    /*
    This method accepts a list of Expense objects and return a HashMap of person, value pair simplifying all the debts across all expenses. 
    */
    public static Map<Person, BigDecimal> sumCredits(List<Expense> expenses){
        Map<Person, BigDecimal> total = new HashMap<>();

        for (Expense expense : expenses) {
            expense.getConvertedCreditsMap().forEach((person, value) -> total.merge(person, value, BigDecimal::add));
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
