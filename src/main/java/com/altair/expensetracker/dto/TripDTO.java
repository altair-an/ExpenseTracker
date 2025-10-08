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
    @JsonProperty("simpleDebts")
    private Map<String, BigDecimal> simpledDebt;
    @JsonProperty("convertedSimpleDebts")
    private Map<String, BigDecimal> simpleDebtConverted;

    public Long getID() { return id;}
    public void setID(Long id) { this.id = id;}

    public String getName() { return tripName;}
    public void setName(String tripName) {this.tripName = tripName;}

    public List<String> getParticipants() {return tripParticipants;}
    public void setParticipants(List<String> tripParticipants) {this.tripParticipants = tripParticipants;}

    public Map<String, BigDecimal> getSimpledDebt() {return simpledDebt;}
    public void setSimpledDebt(Map<String, BigDecimal> simpledDebt) {this.simpledDebt = simpledDebt;}

    public Map<String, BigDecimal> getSimpleDebtConverted() {return simpleDebtConverted;}
    public void setSimpleDebtConverted(Map<String, BigDecimal> simpleDebtConverted) {this.simpleDebtConverted = simpleDebtConverted;}
}
