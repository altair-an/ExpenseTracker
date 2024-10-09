import java.util.*;

public class Person {
    private static int counter;
    private int id;
    private String name;
    private Map<Person, Double> exactDebt = new HashMap<>(); //Storing exact debt so you know from whom and how much you owe

    public Person(){}
    public Person(String name){
        this.name = name;
        counter++;
        id = counter;

    }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    /*
    Setter method to add/update the amount owe for that person using HashMap's merge() method.
    merge() takes in three arguments - a key, a value and a function. Similar for the forEach() method.
    If the key does not exist in the HashMap, merge() will insert the key and value argument like the put() method. 
    If the key exists with a value, merge() will call given function to operate on the amount argument and the current value of the key. 
    Double::sum - Double is the wrapper class, sum is a static method of the wrapper class, and :: allows you to refer the sum method without
    activating/calling it. Allowing you to pass it around as an argument.
    */
    public void addExactDebt(Person person, double amount){
        exactDebt.merge(person, amount, Double::sum);
    }

    public Map<Person, Double> getExactDebt(){
        return exactDebt;
    }

    /*
    public String toString(){  //Needed?
        return "Name: " + name + "\tID: " + id + "\tBalance: " + balance;
    }
    */

    //For debugging while testing the expense class printing out results
    public String toString(){
        return name;
    }


}
