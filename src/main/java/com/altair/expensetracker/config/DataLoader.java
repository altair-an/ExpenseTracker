package com.altair.expensetracker.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.altair.expensetracker.entity.*;
import com.altair.expensetracker.repository.*;

@Configuration
@Profile("dev")
public class DataLoader {

    @Bean
    CommandLineRunner loadTestData(PersonRepository personRepository, 
                                   TripRepository tripRepository, 
                                   ExpenseRepository expenseRepository) {
        return args -> {
            // Create people
            Person henry = new Person("Henry");
            Person van = new Person("Van");
            Person khoa = new Person("Khoa");
            
            personRepository.save(henry);
            personRepository.save(van);
            personRepository.save(khoa);
            List<Person> participants = personRepository.findAll();
            
            // Create trip
            Trip trip = new Trip("Japan");
            trip.addParticipants(participants);

            
            // Create expense
            Expense expense = new Expense("Yakiniku Like", new BigDecimal("6000.00"), LocalDate.now().toString());
            expense.setTrip(trip);
            expense.setParticipants(participants);
            expense.setPayer(khoa, new BigDecimal("4000.00"));
            expense.setPayer(henry, new BigDecimal("2000.00"));
            expense.evenSplit();
            expense.setCurrencyRate(new BigDecimal("148.7")); 
            expense.setTrip(trip);
            trip.addExpense(expense);


            // Create another expense
            Expense expense2 = new Expense();
            expense2.setParticipants(participants);  
            expense2.setTitle("Comodi iida");
            expense2.setAmount(new BigDecimal("3000.00"));
            expense2.setDate(LocalDate.now().toString());

            expense2.setPayer(van, new BigDecimal("3000.00")); 

            expense2.evenSplit();
            expense2.setCurrencyRate(new BigDecimal("148.7")); 
            expense2.setTrip(trip);
            trip.addExpense(expense2);





            tripRepository.save(trip);
        };
    }
}