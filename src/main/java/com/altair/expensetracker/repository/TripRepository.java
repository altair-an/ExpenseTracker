package com.altair.expensetracker.repository;

import com.altair.expensetracker.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
