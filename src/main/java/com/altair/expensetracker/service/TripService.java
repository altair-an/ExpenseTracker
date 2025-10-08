package com.altair.expensetracker.service;
import java.util.*;
import java.math.*;
import org.springframework.stereotype.Service;

import com.altair.expensetracker.entity.*;
import com.altair.expensetracker.repository.*;
import com.altair.expensetracker.dto.*;


@Service
public class TripService {

    private final PersonRepository personRepository;

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;

    public TripService(TripRepository tripRepository, ExpenseRepository expenseRepository, PersonRepository personRepository) {
        this.tripRepository = tripRepository;
        this.expenseRepository = expenseRepository;
        this.personRepository = personRepository;
    }

    public List<TripDTO> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        List<TripDTO> tripDTOs = new ArrayList<>();
        for (Expense expense : expenses) {
            expense.calculateAll();
        }
        for (Trip trip : trips) {
            trip.calculateTotalExpense();
            tripDTOs.add(convertToDTO(trip));
        }
        return tripDTOs;
    }
    public TripDTO getTripById(Long id) {
        Trip trip = tripRepository.findById(id).orElse(null);
        if (trip != null) {
            List<Expense> expenses = trip.getExpenseList();
            for (Expense expense : expenses) {
                expense.calculateAll();
            }
            trip.calculateTotalExpense();
        }
        return convertToDTO(trip);
    }

    public Trip createTrip(TripCreateDTO tripDTO) {
        Trip trip = convertToEntity(tripDTO);
        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    /// DTO-Entity conversion

    // Convert Trip entity to TripDTO
    public TripDTO convertToDTO(Trip trip) {
        TripDTO dto = new TripDTO();
        List<String> particpants = new ArrayList<>();
        Map<String, BigDecimal> simpleDebt = new HashMap<>();
        Map<String, BigDecimal> simpleDebtConverted = new HashMap<>();
        dto.setID(trip.getID());
        dto.setTripName(trip.getTripName());
        for (var person : trip.getParticipants()) {
            particpants.add(person.getName());
        }
        dto.setParticipants(particpants);
        for (var debt : trip.getSimpleDebt().entrySet()) {
            simpleDebt.put(debt.getKey().getName(), debt.getValue());
        }
        dto.setSimpledDebt(simpleDebt);
        for (var debt : trip.getSimpleDebtConverted().entrySet()) {
            simpleDebtConverted.put(debt.getKey().getName(), debt.getValue());
        }
        dto.setSimpleDebtConverted(simpleDebtConverted);
        return dto;
    }

    // Convert TripDTO to Trip entity
    public Trip convertToEntity(TripCreateDTO dto) {
        Trip trip = new Trip();
        trip.setTripName(dto.getTripName());
        List<Person> participants = new ArrayList<>();
        for (PersonDTO personDTO : dto.getParticipants()) {
            Person person;
            person = personRepository.findById(personDTO.getID())
                .orElseThrow(() -> new NoSuchElementException("Person not found with id: " + personDTO.getID()));
            participants.add(person);
        }
        trip.addParticipants(participants);
        return trip;
    }
}
