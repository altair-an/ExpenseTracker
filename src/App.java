import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class App {
    private static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws IOException {

        List<Person> participants = new ArrayList<>();
        List<Expense> expenses = new ArrayList<>();
        System.out.println("Hello World!");
    }

    /*
    This method accepts a list of Expense objects and return a HashMap of person, value pair simplifying all the debts across all expenses. 
    */
    public static Map<Person, Double> sumCredits(List<Expense> expenses){
        Map<Person, Double> total = new HashMap<>();

        for (Expense expense : expenses) {
            expense.getConvertedCreditsMap().forEach((person, value) -> total.merge(person, value, Double::sum));
        }
        return total;
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

    public void writeExpensesToFile(List<Expense> expenses, String fileName) {
        try {
            // Serialize the list of expenses into a JSON file
            mapper.writeValue(new File(fileName), expenses);
            System.out.println("Expenses successfully written to " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
