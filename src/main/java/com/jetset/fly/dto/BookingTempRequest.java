package com.jetset.fly.dto;

import java.util.List;

public class BookingTempRequest {
    private Long airlineId;
    private Long flightId;
    private Long scheduleId;
    private Long flightClassId;
    private Double rate;
    private Integer noOfPassengers;
    private Long userId;
    private List<PassengerDTO> passengers;

    // Default Constructor
    public BookingTempRequest() {
    }

    // Parameterized Constructor
    public BookingTempRequest(Long airlineId, Long flightId, Long scheduleId, Long flightClassId, Double rate, Integer noOfPassengers,Long userId, List<PassengerDTO> passengers) {
        this.airlineId = airlineId;
        this.flightId = flightId;
        this.scheduleId = scheduleId;
        this.flightClassId = flightClassId;
        this.rate = rate;
        this.noOfPassengers = noOfPassengers;
        this.passengers = passengers;
        this.userId=userId;
    }

    public Long getUserId() {
    	return userId;
    }
    
    public void setUserId(Long userId) {
    	this.userId=userId;
    }
    // Getter for airlineId
    public Long getAirlineId() {
        return airlineId;
    }

    // Setter for airlineId
    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    // Getter for flightId
    public Long getFlightId() {
        return flightId;
    }

    // Setter for flightId
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    // Getter for scheduleId
    public Long getScheduleId() {
        return scheduleId;
    }

    // Setter for scheduleId
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
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
    public Double getRate() {
        return rate;
    }

    // Setter for rate
    public void setRate(Double rate) {
        this.rate = rate;
    }

    // Getter for noOfPassengers
    public Integer getNoOfPassengers() {
        return noOfPassengers;
    }

    // Setter for noOfPassengers
    public void setNoOfPassengers(Integer noOfPassengers) {
        this.noOfPassengers = noOfPassengers;
    }

    // Getter for passengers
    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    // Setter for passengers
    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }
}