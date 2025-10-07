package com.altair.expensetracker.service;

import com.altair.expensetracker.repository.*;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import com.altair.expensetracker.entity.*;
import java.math.*;
import java.util.*;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;

    public ExpenseService(ExpenseRepository expenseRepository, TripRepository tripRepository) {
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        // recalculate derived fields
        for (Expense expense : expenses) {
            calculateAll(expense);
        }
        return expenses;
    }

    public Expense getExpenseById(Long id) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            calculateAll(expense); // Recalculate derived fields
            return expense;
        } else {
            throw new NoSuchElementException("Expense not found with id: " + id);
        }
    }

    public Expense createExpense(Expense expense) {
        calculateAll(expense);
        return expenseRepository.save(expense);
    }

    public Expense createExpenseWithEvenSplit(Expense expense) {
        generateEvenSplits(expense);
        calculateAll(expense);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense existingExpense = optionalExpense.get();
            
            // Update 
            existingExpense.setAmount(updatedExpense.getAmount());
            existingExpense.setTitle(updatedExpense.getTitle());
            existingExpense.setDate(updatedExpense.getDate());
            existingExpense.setExchangeRate(updatedExpense.getExchangeRate());
            existingExpense.getPayerList().clear();
            existingExpense.getPayerList().addAll(updatedExpense.getPayerList());
            existingExpense.getSplitsList().clear();
            existingExpense.getSplitsList().addAll(updatedExpense.getSplitsList());
            
            // Recalculate 
            calculateAll(existingExpense);
            
            return expenseRepository.save(existingExpense);
        } else {
            throw new NoSuchElementException("Expense not found with id: " + id);
        }
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    private void generateEvenSplits(Expense expense) {
        List<Person> people = expense.getExpenseParticipants();
        BigDecimal amountPerPerson = expense.getAmount().divide(new BigDecimal(people.size()), 
            3, 
            RoundingMode.HALF_UP);
        expense.getSplitsList().clear();
        for (Person person : people) {
            Splits split = new Splits();
            split.setPerson(person);
            split.setAmount(amountPerPerson);
            split.setExpense(expense);
            expense.getSplitsList().add(split);
        }
    }

    // For nested Trip-Expense creation
    public Expense createExpenseForTrip(Long tripId, Expense expense) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found"));
        
        expense.setTrip(trip);    
        calculateAll(expense);
        return expenseRepository.save(expense);
    }
    
    public List<Expense> getExpensesForTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found"));
        List<Expense> expenses = trip.getExpenseList();
        for (Expense expense : expenses) {
            calculateAll(expense);
        }
        return expenses;
    }

    public Expense getExpenseInTrip(Long tripId, Long expenseId) {
        // Find trip
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found"));
        
        // Find expense 
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        
        // Check if expense belongs to this trip
        if (!expense.getTrip().getId().equals(tripId)) {
            throw new EntityNotFoundException("Expense does not belong to this trip");
        }

        //expense.calculateExpense();
        calculateAll(expense);
        return expense;
    }




    ////// CALCULATION LOGIC //////
    
    // Update payerMap and splitsMap transient/derived data from the lists
    private void updateMaps(Expense expense) {
        expense.getPayerMap().clear();
        expense.getSplitsMap().clear();
        for (Payer payer : expense.getPayerList()) {
            expense.getPayerMap().merge(payer.getPerson(), payer.getAmount(), BigDecimal::add);
        }
        for (Splits split : expense.getSplitsList()) {
            expense.getSplitsMap().merge(split.getPerson(), split.getAmount(), BigDecimal::add);
        }
    }

    // Calculate expenseBalance and expenseBalanceConverted from payerMap and splitsMap
    private void calculateExpenseBalance(Expense expense) {
        // Getting references to data inside Expense object
        Map<Person, BigDecimal> payerMap = expense.getPayerMap();
        Map<Person, BigDecimal> splitsMap = expense.getSplitsMap();
        Map<Person, BigDecimal> expenseBalance = expense.getExpenseBalance();
        Map<Person, BigDecimal> expenseBalanceConverted = expense.getExpenseBalanceConverted();
        BigDecimal exchangeRate = expense.getExchangeRate();
        List<Person> participants = expense.getExpenseParticipants();
        
        updateMaps(expense); // Calculate payerMap and splitsMap from the lists
        
        for (Person person : participants) {
            BigDecimal payerAmount = payerMap.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal splitAmount = splitsMap.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal balance = splitAmount.subtract(payerAmount);

            expenseBalance.put(person, balance);
            
            BigDecimal balanceExchanged = balance.divide(exchangeRate, 3, RoundingMode.HALF_UP);
            expenseBalanceConverted.put(person, balanceExchanged);
        }
    }

    private void calculateIndividualBalances(Expense expense) {
        // Finding the borrower (positive value)
        for (Map.Entry<Person, BigDecimal> entry : expense.getExpenseBalance().entrySet()) {
            Person borrowPerson = entry.getKey();
            BigDecimal borrowedAmount = entry.getValue();

            if (borrowedAmount.compareTo(BigDecimal.ZERO) > 0) { // Find the borrower if positive value
                // Finding the lender (negative value)
                for (Map.Entry<Person, BigDecimal> entryB : expense.getExpenseBalance().entrySet()) {
                    Person lendPerson = entryB.getKey();
                    BigDecimal lentAmount = entryB.getValue();

                    if (borrowedAmount.compareTo(BigDecimal.ZERO) <= 0) break;

                    if (lentAmount.compareTo(BigDecimal.ZERO) < 0) {
                        BigDecimal absLentAmount = lentAmount.abs(); // Get the absolute value of the lent amount and find the min
                        BigDecimal debtValue = absLentAmount.min(borrowedAmount); 

                        borrowPerson.getIndividualBalance().merge(lendPerson, debtValue, BigDecimal::add); // Add the debt to borrowPerson's individualBalance and converted maps
                        BigDecimal convertedDebtValue = debtValue.divide(expense.getExchangeRate(), 3, RoundingMode.HALF_UP);
                        borrowPerson.getBalanceConverted().merge(lendPerson, convertedDebtValue, BigDecimal::add);
                        
                        borrowedAmount = borrowedAmount.subtract(debtValue);  // Settling the the debt this round. Loop will continue until borrowedAmount is 0 or less.
                        expense.getExpenseBalance().put(lendPerson, lentAmount.add(debtValue)); // Update the expenseBalance for lenderPerson to reflect settled debt
                    }
                }
                expense.getExpenseBalance().put(borrowPerson, borrowedAmount); // Update expenseBalance for borrowPerson with the remaining balance after settling debts
            }
        }
        calculateExpenseBalance(expense); // Recalculating the expense balance HashMap back to original values
    }

    // Calculate all derived fields in one go
    public void calculateAll(Expense expense) {
        calculateExpenseBalance(expense);
        calculateIndividualBalances(expense);
    }

}
