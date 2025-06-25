package com.altair.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.altair.expensetracker.entity.*;
import com.altair.expensetracker.repository.ExpenseRepository;
import com.altair.expensetracker.repository.PersonRepository;
import com.altair.expensetracker.repository.TripRepository;

@SpringBootApplication
public class ExpenseTrackerApplication {

    private final ExpenseRepository expenseRepository;

    ExpenseTrackerApplication(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }


    @Bean
    CommandLineRunner runner(PersonRepository personRepository, TripRepository tripRepository) {
        return args -> {
            Trip trip = new Trip("Japan");

            personRepository.save(new Person("Henry"));
            personRepository.save(new Person("Van"));
            personRepository.save(new Person("Khoa"));
            List<Person> participants = personRepository.findAll();

            Expense expense1 = new Expense();
            expense1.setParticipants(participants);
            expense1.setTitle("Yakiniku Like");
            expense1.setAmount(new BigDecimal("6000.00"));
            expense1.setDate(LocalDate.now().toString());
            expense1.setPayer(participants.get(0), new BigDecimal("2000.00"));  // henry paid 500
            expense1.setPayer(participants.get(2), new BigDecimal("4000.00")); // khoa paid 2500
            expense1.evenSplit();
            expense1.setCurrencyRate(new BigDecimal("148.7")); //setting rate
            
            expense1.calculateExpense();
            expense1.calculateIndividualBalances();
            trip.addExpense(expense1);
            trip.addParticipants(participants);
            expense1.setTrip(trip); 
            tripRepository.save(trip);

            expenseRepository.save(expense1);
        };
    }
}

// mvn spring-boot:run
// http://localhost:8080/api/persons  
// http://localhost:8080/api/expenses





