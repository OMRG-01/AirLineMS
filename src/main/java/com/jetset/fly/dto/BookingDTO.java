package com.jetset.fly.dto;

public class BookingDTO {
    private Long userId;
    private Long airlineId;
    private Long flightId;
    private Long scheduleId;
    private Long flightClassId;

    // Default Constructor
    public BookingDTO() {
    }

    // Parameterized Constructor
    public BookingDTO(Long userId, Long airlineId, Long flightId, Long scheduleId, Long flightClassId) {
        this.userId = userId;
        this.airlineId = airlineId;
        this.flightId = flightId;
        this.scheduleId = scheduleId;
        this.flightClassId = flightClassId;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public Long getFlightClassId() {
        return flightClassId;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setFlightClassId(Long flightClassId) {
        this.flightClassId = flightClassId;
    }
}