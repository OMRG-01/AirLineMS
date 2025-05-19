package com.jetset.fly.model;

import jakarta.persistence.*;

@Entity
public class FlightScheduleRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private AirFlight flight;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private FlightSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class flightClass;
    
    

    @Column(nullable = false)
    private Double rate;
    
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public AirFlight getFlight() {
        return flight;
    }

    public void setFlight(AirFlight flight) {
        this.flight = flight;
    }

    public FlightSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(FlightSchedule schedule) {
        this.schedule = schedule;
    }

    public Class getFlightClass() {
        return flightClass;
    }

    public void setFlightClass(Class flightClass) {
        this.flightClass = flightClass;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
