package com.altair.expensetracker.service;

import com.altair.expensetracker.repository.*;
import com.altair.expensetracker.entity.*;
import com.altair.expensetracker.dto.ExpenseCreateDTO;
import com.altair.expensetracker.dto.ExpenseCreateDTO.PersonDTO;
import com.altair.expensetracker.dto.ExpenseDTO;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final PersonRepository personRepository;

    public ExpenseService(ExpenseRepository expenseRepository, TripRepository tripRepository, PersonRepository personRepository) {
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
        this.personRepository = personRepository;
    }

    // Retrieve all expenses regardless of trip association
    public List<ExpenseDTO> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        List<ExpenseDTO> expenseDTOs = new ArrayList<>();
        for (Expense expense : expenses) {
            calculateAll(expense); // Recalculate derived fields
            expenseDTOs.add(convertToDTO(expense));
        }
        return expenseDTOs;
    }

    // Retrieve a single expense by ID
    public ExpenseDTO getExpenseByID(Long id) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            calculateAll(expense); // Recalculate derived fields
            return convertToDTO(expense);
        } else {
            throw new NoSuchElementException("Expense not found with id: " + id);
        }
    }

    // Create a new expense
    public ExpenseDTO createExpense(Expense expense) {
        calculateAll(expense);
        return convertToDTO(expenseRepository.save(expense));
    }

    // Create a new expense with even splits
    public Expense createExpenseWithEvenSplit(Expense expense) {
        generateEvenSplits(expense);
        calculateAll(expense);
        return expenseRepository.save(expense);
    }

    // Update an existing expense
    public Expense updateExpense(Long id, Expense updatedExpense) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense existingExpense = optionalExpense.get();

            // Update fields
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

    // Delete an expense by ID
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // Helper method for even splits
    private void generateEvenSplits(Expense expense) {
        List<Person> people = expense.getParticipants();
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
    public ExpenseDTO createExpenseForTrip(Long tripId, ExpenseCreateDTO expenseCreateDTO) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found"));
        
        Expense expense = convertCreateDTOToEntity(expenseCreateDTO);
        expense.setTrip(trip);    
        calculateAll(expense);
        Expense saved = expenseRepository.save(expense);
        return convertToDTO(saved);
    }

    // Get all expenses for a specific trip
    public List<ExpenseDTO> getExpensesForTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found"));
        List<Expense> expenses = trip.getExpenseList();
        List<ExpenseDTO> expenseDTOs = new ArrayList<>();
        for (Expense expense : expenses) {
            calculateAll(expense);
            expenseDTOs.add(convertToDTO(expense));
        }
        return expenseDTOs;
    }

    // Get a specific expense within a trip
    public ExpenseDTO getExpenseInTrip(Long tripId, Long expenseId) {
        // Find trip (to throw if not found)
        tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        ExpenseDTO dto = getExpenseByID(expenseId);

        if (dto.getTripID() == null || !dto.getTripID().equals(tripId)) {
            throw new EntityNotFoundException("Expense does not belong to this trip");
        }
        return dto;
    }






    
    ////// DTO CONVERSION LOGIC //////
    
    // DTO : Converting Expense entity to ExpenseDTO for GET requests
    public ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setID(expense.getID());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setCurrencyCode(expense.getCurrencyCode());
        dto.setExchangeRate(expense.getExchangeRate());
        dto.setTripID(expense.getTrip() != null ? expense.getTrip().getID() : null);
        
        List<String> participantNames = new ArrayList<>();
        for (Person person : expense.getParticipants()) {
            participantNames.add(person.getName());
        }
        dto.setExpenseParticipants(participantNames);
        
        Map<String, BigDecimal> payersMap = new HashMap<>();
        for (Map.Entry<Person, BigDecimal> entry : expense.getPayerMap().entrySet()) {
            payersMap.put(entry.getKey().getName(), entry.getValue());
        }
        dto.setPayersMap(payersMap);
        
        Map<String, BigDecimal> splitsMap = new HashMap<>();
        for (Map.Entry<Person, BigDecimal> entry : expense.getSplitsMap().entrySet()) {
            splitsMap.put(entry.getKey().getName(), entry.getValue());
        }
        dto.setSplitsMap(splitsMap);
        
        Map<String, BigDecimal> expenseBalance = new HashMap<>();
        for (Map.Entry<Person, BigDecimal> entry : expense.getExpenseBalance().entrySet()) {
            expenseBalance.put(entry.getKey().getName(), entry.getValue());
        }
        dto.setExpenseBalance(expenseBalance);
        
        Map<String, BigDecimal> expenseBalanceConverted = new HashMap<>();
        for (Map.Entry<Person, BigDecimal> entry : expense.getExpenseBalanceConverted().entrySet()) {
            expenseBalanceConverted.put(entry.getKey().getName(), entry.getValue());
        }
        dto.setExpenseBalanceConverted(expenseBalanceConverted);

        return dto;
    }

    // DTO : Converting ExpenseCreateDTO to Expense entity for POST requests
    public Expense convertCreateDTOToEntity(ExpenseCreateDTO dto) {
        Expense expense = new Expense();
        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setExchangeRate(dto.getExchangeRate());
        
        // Participants
        List<Person> participants = new ArrayList<>();
        for (PersonDTO personDTO : dto.getExpenseParticipants()) {
            Person person;
            person = personRepository.findById(personDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Person not found with id: " + personDTO.getId()));
            participants.add(person);
        }
        expense.setParticipants(participants);

        // Payers
        for (ExpenseCreateDTO.PayerDTO payerDTO : dto.getPayerList()) {
            Person person = personRepository.findById(payerDTO.getPerson().getId())
                .orElseThrow(() -> new NoSuchElementException("Person not found with id: " + payerDTO.getPerson().getId()));
            expense.setPayer(person, payerDTO.getAmount());
        }

        // Splits
        for (ExpenseCreateDTO.SplitsDTO splitDTO : dto.getSplitsList()) {
            Person person = personRepository.findById(splitDTO.getPerson().getId())
                .orElseThrow(() -> new NoSuchElementException("Person not found with id: " + splitDTO.getPerson().getId()));
            expense.setSplit(person, splitDTO.getAmount());
        }

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
        List<Person> participants = expense.getParticipants();
        
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

    // Calculate debts and debtsConverted for each participant based on expenseBalance
    private void calculateDebts(Expense expense) {
        // Finding the borrower (positive value)
        for (Map.Entry<Person, BigDecimal> entry : expense.getExpenseBalance().entrySet()) {
            Person borrower = entry.getKey();
            BigDecimal borrowedAmount = entry.getValue();

            if (borrowedAmount.compareTo(BigDecimal.ZERO) > 0) { // Find the borrower if positive value
                // Finding the lender (negative value)
                for (Map.Entry<Person, BigDecimal> entryB : expense.getExpenseBalance().entrySet()) {
                    Person lender = entryB.getKey();
                    BigDecimal lentAmount = entryB.getValue();

                    if (borrowedAmount.compareTo(BigDecimal.ZERO) <= 0) break;

                    if (lentAmount.compareTo(BigDecimal.ZERO) < 0) {
                        BigDecimal absLentAmount = lentAmount.abs(); // Get the absolute value of the lent amount and find the min
                        BigDecimal debtValue = absLentAmount.min(borrowedAmount); 

                        //borrower.getDebts().merge(lender, debtValue, BigDecimal::add); // Add the debt to borrower's debts and converted maps
                        borrower.addDebt(lender, debtValue);  // Add the debt to borrower's debts and converted maps

                        BigDecimal convertedDebtValue = debtValue.divide(expense.getExchangeRate(), 3, RoundingMode.HALF_UP);
                        //borrower.getDebtsConverted().merge(lender, convertedDebtValue, BigDecimal::add);
                        borrower.addDebtConverted(lender, convertedDebtValue);
                        
                        borrowedAmount = borrowedAmount.subtract(debtValue);  // Settling the the debt this round. Loop will continue until borrowedAmount is 0 or less.
                        expense.getExpenseBalance().put(lender, lentAmount.add(debtValue)); // Update the expenseBalance for lender to reflect settled debt
                    }
                }
                expense.getExpenseBalance().put(borrower, borrowedAmount); // Update expenseBalance for borrower with the remaining balance after settling debts
            }
        }
        calculateExpenseBalance(expense); // Recalculating the expense balance HashMap back to original values
    }

    // Calculate all derived fields in one go
    public void calculateAll(Expense expense) {
        calculateExpenseBalance(expense);
        calculateDebts(expense);
    }



}
