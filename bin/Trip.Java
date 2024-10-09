import java.util.*;

public class Trip {
    private String name;
    private Map<Integer, Person> people;
    private List<Expense> expenses;

    public Trip (String name){
        this.name = name;
    }

    //Add new participant to the group if needed
    public void addPerson(Person person){}

    //Add an Expense object to the Expense array
    public void addExpense(Expense expense){}

    //Return the list of all participants
    public Map<Integer, Person> getPeople(){}

    //Return the expenses array
    public List<Expense> getExpenses(){}

    //Return the total of all expenses? 
    public double calculateTotalExpense(){}

    
}