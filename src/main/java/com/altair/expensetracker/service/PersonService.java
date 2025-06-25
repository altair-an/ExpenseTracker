package com.altair.expensetracker.service;

import com.altair.expensetracker.entity.Expense;
import com.altair.expensetracker.entity.Person;
import com.altair.expensetracker.repository.PersonRepository;
import com.altair.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonService {

    // This class will handle the business logic related to Person entities.
    // It will interact with the PersonRepository to perform CRUD operations.

    private final PersonRepository personRepository;
    private final ExpenseRepository expenseRepository;
    public PersonService(PersonRepository personRepository, ExpenseRepository expenseRepository) {
        this.personRepository = personRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<Person> getAllPersons() {
        List<Expense> expenses = expenseRepository.findAll();
        // recalculate derived fields
        for (Expense expense : expenses) {
            expense.calculateAll();
        }
        return personRepository.findAll();
    }

    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
    
}
