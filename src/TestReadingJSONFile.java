import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class TestReadingJSONFile {
    private static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {
        String filename = "expense.json";
        List<Expense> expenses = readExpensesFromFile(filename);

        System.out.println("\n~~~~~Printing expenses List: ~~~~~~");
        System.out.println(expenses);
        System.out.println("\n~~~~~Printing expense at index 0: ~~~~~~");
        System.out.println(expenses.get(0));
        
        //getting participants from the expense using Expense's getter
        List<Person> participants = expenses.get(0).getParticipants();
        System.out.println("\n~~~~~~Printing participants~~~~~~");
        System.out.println(participants);
        //single out henry
        Person henry = participants.get(0);
        System.out.println("\n~~~~~~Printint Henry's debt: ~~~~~~");
        System.out.println(henry.getExactDebt());



        //Expense #1 - Manually setting payers and setting splits
        String title = "Cocoichiban";
        double amount = 3000.00;
        String date = LocalDate.now().toString();
        Expense e1 = new Expense(title, amount, date);
        expenses.add(e1);
        e1.setParticipants(participants);

        e1.setPayer(henry, 500);
        e1.setPayer(participants.get(2), 2500);

            //Manually setting split responsibilities one at the time
        e1.setSplit(henry, 1000); 
        e1.setSplit(participants.get(1), 1000);
        e1.setSplit(participants.get(2), 1000);

        e1.setRate(148.7); 

        e1.calculateCredits();
        e1.calculateExactDebt();  
        System.out.println("\n\n" + e1);

        System.out.println("JPY to USD: " + e1.getConvertedCreditsMap());
    }


    public static List<Expense> readExpensesFromFile(String fileName) {
        try {
            // Deserialize JSON file into a list of Expense objects
            return mapper.readValue(new File(fileName), 
                    mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
