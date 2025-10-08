package com.altair.expensetracker.dto;
import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TripDTO {
    @JsonProperty("tripId")
    private Long id;
    private String tripName;
    @JsonProperty("participants")
    private List<String> tripParticipants;
    @JsonProperty("simplifiedDebts")
    private Map<String, BigDecimal> tripSimplifiedDebt;
    @JsonProperty("convertedSimplifiedDebts")
    private Map<String, BigDecimal> tripSimplifiedDebtConverted;

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getTripName() { return tripName;}
    public void setTripName(String tripName) {this.tripName = tripName;}

    public List<String> getTripParticipants() {return tripParticipants;}
    public void setTripParticipants(List<String> tripParticipants) {this.tripParticipants = tripParticipants;}

    public Map<String, BigDecimal> getTripSimplifiedDebt() {return tripSimplifiedDebt;}
    public void setTripSimplifiedDebt(Map<String, BigDecimal> tripSimplifiedDebt) {this.tripSimplifiedDebt = tripSimplifiedDebt;}

    public Map<String, BigDecimal> getTripSimplifiedDebtConverted() {return tripSimplifiedDebtConverted;}
    public void setTripSimplifiedDebtConverted(Map<String, BigDecimal> tripSimplifiedDebtConverted) {this.tripSimplifiedDebtConverted = tripSimplifiedDebtConverted;}
}
