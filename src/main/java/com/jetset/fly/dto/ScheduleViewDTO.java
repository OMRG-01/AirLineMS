package com.jetset.fly.dto;
import java.time.LocalDateTime;
import java.util.Map;

public class ScheduleViewDTO {
    private Long id;
    private String airlineName;
    private String flightNumber;
    private String sourceCity;
    private String destinationCity;
    private LocalDateTime departAt;
    private LocalDateTime arriveAt;
    private Map<String, Double> classRates; // className -> rate

    // Default Constructor
    public ScheduleViewDTO() {
    }

    // Parameterized Constructor
    public ScheduleViewDTO(Long id, String airlineName, String flightNumber, String sourceCity, String destinationCity, LocalDateTime departAt, LocalDateTime arriveAt, Map<String, Double> classRates) {
        this.id = id;
        this.airlineName = airlineName;
        this.flightNumber = flightNumber;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.departAt = departAt;
        this.arriveAt = arriveAt;
        this.classRates = classRates;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for airlineName
    public String getAirlineName() {
        return airlineName;
    }

    // Setter for airlineName
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    // Getter for flightNumber
    public String getFlightNumber() {
        return flightNumber;
    }

    // Setter for flightNumber
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    // Getter for sourceCity
    public String getSourceCity() {
        return sourceCity;
    }

    // Setter for sourceCity
    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    // Getter for destinationCity
    public String getDestinationCity() {
        return destinationCity;
    }

    // Setter for destinationCity
    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    // Getter for departAt
    public LocalDateTime getDepartAt() {
        return departAt;
    }

    // Setter for departAt
    public void setDepartAt(LocalDateTime departAt) {
        this.departAt = departAt;
    }

    // Getter for arriveAt
    public LocalDateTime getArriveAt() {
        return arriveAt;
    }

    // Setter for arriveAt
    public void setArriveAt(LocalDateTime arriveAt) {
        this.arriveAt = arriveAt;
    }

    // Getter for classRates
    public Map<String, Double> getClassRates() {
        return classRates;
    }

    // Setter for classRates
    public void setClassRates(Map<String, Double> classRates) {
        this.classRates = classRates;
    }
}