package com.altair.expensetracker.controller;

import com.altair.expensetracker.entity.Person;
import com.altair.expensetracker.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// mvn spring-boot:run

@RestController
@RequestMapping("/api/persons")  // http://localhost:8080/api/persons    
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable Long id) {
        return personService.getPersonById(id);
    }

    // Command below in powershell to test this endpoint:
    // Invoke-RestMethod -Uri "http://localhost:8080/api/persons" -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"name":"Alice"}'
    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personService.createPerson(person);
    }
    

}
