package com.jetset.fly.dto;

import java.util.List;

import java.math.BigDecimal; // Import for BigDecimal

public class ConnectedBookingDTO {
    private Long scheduleFlightId;
    private Long flightClassId;
    private BigDecimal rate; // Changed to BigDecimal for precise currency handling
    private Long userId;
    private Integer noOfPassengers; // Corrected field name from nos_passanger

    // Default Constructor
    public ConnectedBookingDTO() {
    }

    // Parameterized Constructor
    public ConnectedBookingDTO(Long scheduleFlightId, Long flightClassId, BigDecimal rate, Long userId, Integer noOfPassengers) {
        this.scheduleFlightId = scheduleFlightId;
        this.flightClassId = flightClassId;
        this.rate = rate;
        this.userId = userId;
        this.noOfPassengers = noOfPassengers;
    }

    // Getter for scheduleFlightId
    public Long getScheduleFlightId() {
        return scheduleFlightId;
    }

    // Setter for scheduleFlightId
    public void setScheduleFlightId(Long scheduleFlightId) {
        this.scheduleFlightId = scheduleFlightId;
    }

    // Getter for flightClassId
    public Long getFlightClassId() {
        return flightClassId;
    }

    // Setter for flightClassId
    public void setFlightClassId(Long flightClassId) {
        this.flightClassId = flightClassId;
    }

    // Getter for rate
    public BigDecimal getRate() {
        return rate;
    }

    // Setter for rate
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    // Getter for userId
    public Long getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter for noOfPassengers
    public Integer getNoOfPassengers() {
        return noOfPassengers;
    }

    // Setter for noOfPassengers
    public void setNoOfPassengers(Integer noOfPassengers) {
        this.noOfPassengers = noOfPassengers;
    }
}