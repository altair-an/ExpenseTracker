package com.altair.expensetracker.dto;

import java.util.List;

/*
 * Example JSON request for creating a trip:
 {
   "tripName": "Europe Trip",
   "participants": [
     { "id": 1, "name": "Person A" },
     { "id": 2, "name": "Person B" },
     { "id": 3, "name": "Person C" }
   ]
 }
 */

public class TripCreateDTO {
    private String tripName;
    private List<PersonDTO> participants;

    // Getters and setters
    public String getTripName() { return tripName; }
    public void setTripName(String tripName) { this.tripName = tripName; }
    public List<PersonDTO> getParticipants() { return participants; }
    public void setParticipants(List<PersonDTO> participants) { this.participants = participants; }
}
