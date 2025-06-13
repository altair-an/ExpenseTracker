import java.util.*;

public class Trip {
    private String trip_name;
    private List<Person> trip_participants;
    private List<Expense> expense_list;

    public Trip (String trip_name){
        this.trip_name = trip_name;
        this.trip_participants = new ArrayList<>();
        this.expense_list = new ArrayList<>();
    }

    public void addPerson(String person_name){
        Person person = new Person(person_name);
        trip_participants.add(person);
    }

    //Add an Expense object to the Expense array
    public void addExpense(Expense expense){
        expense_list.add(expense);
    }

    //Return the list of all participants
    public List<Person> getParticipantsList(){
        return trip_participants;
    }

    //Return the expenses array
    public List<Expense> getExpenseList(){
        return expense_list;
    }

    //Return the total of all expenses? 
    public void calculateTotalExpense(){}

    
}