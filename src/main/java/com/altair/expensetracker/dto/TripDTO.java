package com.altair.expensetracker.dto;
import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Example JSON response for a trip:
 {
   "tripId": 1,
   "tripName": "Europe Trip",
   "participants": [
     "Person A",
     "Person B",
     "Person C"
   ],
   "simpleDebts": {
     "Person B": 0.00,
     "Person C": 1000.00,
     "Person A": -1000.00
   },
   "convertedSimpleDebts": {
     "Person B": 0.000,
     "Person C": 6.757,
     "Person A": -6.757
   }
 }
 */

public class TripDTO {
    @JsonProperty("tripId")
    private Long id;
    private String tripName;
    @JsonProperty("participants")
    private List<String> participants;
    @JsonProperty("simpleDebts")
    private Map<String, BigDecimal> simpledDebt;
    @JsonProperty("convertedSimpleDebts")
    private Map<String, BigDecimal> simpleDebtConverted;

    public Long getID() { return id;}
    public void setID(Long id) { this.id = id;}

    public String getTripName() { return tripName;}
    public void setTripName(String tripName) {this.tripName = tripName;}

    public List<String> getParticipants() {return participants;}
    public void setParticipants(List<String> tripParticipants) {this.participants = tripParticipants;}

    public Map<String, BigDecimal> getSimpledDebt() {return simpledDebt;}
    public void setSimpledDebt(Map<String, BigDecimal> simpledDebt) {this.simpledDebt = simpledDebt;}

    public Map<String, BigDecimal> getSimpleDebtConverted() {return simpleDebtConverted;}
    public void setSimpleDebtConverted(Map<String, BigDecimal> simpleDebtConverted) {this.simpleDebtConverted = simpleDebtConverted;}
}
