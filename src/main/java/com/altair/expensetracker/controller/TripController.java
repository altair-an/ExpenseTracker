package com.altair.expensetracker.controller;

import com.altair.expensetracker.entity.Trip;
import com.altair.expensetracker.service.ExpenseService;
import com.altair.expensetracker.service.TripService;
import com.altair.expensetracker.dto.ExpenseCreateDTO;
import com.altair.expensetracker.dto.ExpenseDTO;
import com.altair.expensetracker.dto.TripDTO;

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
    public List<TripDTO> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public TripDTO getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripService.createTrip(trip);
    }

    // Creating an expense for a specific trip at the endpoint /api/trips/{tripId}/expenses
    // Accepts ExpenseCreateDTO and returns ExpenseDTO
    @PostMapping(value = "/{tripId}/expenses", consumes = "application/json")
    public ResponseEntity<ExpenseDTO> createExpenseForTrip(@PathVariable Long tripId, @RequestBody ExpenseCreateDTO expenseCreateDTO) {
        try {
            ExpenseDTO created = expenseService.createExpenseForTrip(tripId, expenseCreateDTO); // Expense service handles conversion and saving
            return ResponseEntity.status(201).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Getting all expenses for a specific trip at the endpoint /api/trips/{tripId}/expenses
    // Returns ExpenseDTO objects
    @GetMapping("/{tripId}/expenses")
    public ResponseEntity<List<ExpenseDTO>> getAllExpensesForTrip(@PathVariable Long tripId) {
        try {
            List<ExpenseDTO> expenses = expenseService.getExpensesForTrip(tripId); // Expense service fetches and converts
            return ResponseEntity.ok(expenses);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Getting a specific expense for a specific trip at the endpoint /api/trips/{tripId}/expenses/{expenseId}
    // Returns ExpenseDTO object
    @GetMapping("/{tripId}/expenses/{expenseId}")
    public ResponseEntity<ExpenseDTO> getExpenseForTrip(@PathVariable Long tripId, @PathVariable Long expenseId) {
        try {
            ExpenseDTO expense = expenseService.getExpenseInTrip(tripId, expenseId); // Expense service fetches and converts
            return ResponseEntity.ok(expense);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
