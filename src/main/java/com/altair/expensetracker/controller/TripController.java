package com.altair.expensetracker.controller;

import com.altair.expensetracker.entity.Expense;
import com.altair.expensetracker.entity.Trip;
import com.altair.expensetracker.service.ExpenseService;
import com.altair.expensetracker.service.TripService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")  // http://localhost:8080/api/trips
public class TripController {
    private final TripService tripService;
    private final ExpenseService expenseService;

    public TripController(TripService tripService, ExpenseService expenseService) {
        this.tripService = tripService;
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public Trip getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripService.createTrip(trip);
    }

    // Nested Trip-Expense
    @PostMapping("/{tripId}/expenses")
    public ResponseEntity<Expense> createExpenseForTrip(@PathVariable Long tripId, @RequestBody Expense expense) {
        try {
            Expense created = expenseService.createExpenseForTrip(tripId, expense);
            return ResponseEntity.status(201).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{tripId}/expenses")
    public ResponseEntity<List<Expense>> getExpensesForTrip(@PathVariable Long tripId) {
        try {
            List<Expense> expenses = expenseService.getExpensesForTrip(tripId);
            return ResponseEntity.ok(expenses);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{tripId}/expenses/{expenseId}")
    public ResponseEntity<Expense> getExpenseInTrip(@PathVariable Long tripId, @PathVariable Long expenseId) {
        try {
            Expense expense = expenseService.getExpenseInTrip(tripId, expenseId);
            return ResponseEntity.ok(expense);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
