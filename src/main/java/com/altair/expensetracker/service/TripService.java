package com.altair.expensetracker.service;

import org.springframework.stereotype.Service;

import com.altair.expensetracker.entity.Expense;
import com.altair.expensetracker.entity.Trip;
import com.altair.expensetracker.repository.ExpenseRepository;
import com.altair.expensetracker.repository.TripRepository;
import java.util.*;
import java.math.*;
import com.altair.expensetracker.dto.TripDTO;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;

    public TripService(TripRepository tripRepository, ExpenseRepository expenseRepository) {
        this.tripRepository = tripRepository;
        this.expenseRepository = expenseRepository;
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

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public void deleteExpense(Long id) {
        tripRepository.deleteById(id);
    }

    public TripDTO convertToDTO(Trip trip) {
        TripDTO dto = new TripDTO();
        List<String> particpants = new ArrayList<>();
        Map<String, BigDecimal> simpleDebt = new HashMap<>();
        Map<String, BigDecimal> simpleDebtConverted = new HashMap<>();
        dto.setID(trip.getID());
        dto.setName(trip.getName());
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
}
