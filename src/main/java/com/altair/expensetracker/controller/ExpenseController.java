package com.altair.expensetracker.controller;

import com.altair.expensetracker.entity.Expense;
import com.altair.expensetracker.service.ExpenseService;
import com.altair.expensetracker.dto.ExpenseDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/expenses")  // http://localhost:8080/api/expenses
public class ExpenseController {
    private final ExpenseService expenseService;
    
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseDTO> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public ExpenseDTO getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @PostMapping
    public ExpenseDTO createExpense(@RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    @PostMapping("/even-split")
    public Expense createExpenseWithEvenSplit(@RequestBody Expense expense) {
        return expenseService.createExpenseWithEvenSplit(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense) {
        try {
            Expense expense = expenseService.updateExpense(id, updatedExpense);
            return ResponseEntity.ok(expense);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
