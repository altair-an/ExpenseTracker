package com.altair.expensetracker.service;

import org.springframework.stereotype.Service;

import com.altair.expensetracker.entity.Expense;
import com.altair.expensetracker.entity.Trip;
import com.altair.expensetracker.repository.ExpenseRepository;
import com.altair.expensetracker.repository.TripRepository;
import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;

    public TripService(TripRepository tripRepository, ExpenseRepository expenseRepository) {
        this.tripRepository = tripRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<Trip> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        for (Expense expense : expenses) {
            expense.calculateAll();
        }
        for (Trip trip : trips) {
            trip.calculateTotalExpense();
        }
        return trips;
    }
    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public void deleteExpense(Long id) {
        tripRepository.deleteById(id);
    }
}
