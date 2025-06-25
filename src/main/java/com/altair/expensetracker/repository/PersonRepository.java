package com.altair.expensetracker.repository;

import com.altair.expensetracker.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {}