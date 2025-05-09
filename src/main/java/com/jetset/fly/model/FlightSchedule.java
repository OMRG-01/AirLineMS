package com.jetset.fly.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FlightSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private City source;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private City destination;

    @Column(name = "depart_at", nullable = false)
    private LocalDateTime departAt;

    @Column(name = "arrive_at", nullable = false)
    private LocalDateTime arriveAt;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private AirFlight flight;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "airline_id", nullable = false)
    private Long airlineId;

    @Column(nullable = false)
    private String status = "ACTIVE"; // default to ACTIVE

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Optional: createdAt tracking
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public City getSource() {
        return source;
    }

    public void setSource(City source) {
        this.source = source;
    }

    public City getDestination() {
        return destination;
    }

    public void setDestination(City destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartAt() {
        return departAt;
    }

    public void setDepartAt(LocalDateTime departAt) {
        this.departAt = departAt;
    }

    public LocalDateTime getArriveAt() {
        return arriveAt;
    }

    public void setArriveAt(LocalDateTime arriveAt) {
        this.arriveAt = arriveAt;
    }

    public AirFlight getFlight() {
        return flight;
    }

    public void setFlight(AirFlight flight) {
        this.flight = flight;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
