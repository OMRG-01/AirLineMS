package com.jetset.fly.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "air_flight")
public class AirFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "airline_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 
    private Airline airline;

    @Column(nullable = false, unique = true)
    private String fnumber;

    @Column(nullable = false)
    private int totalSeat;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE or DELETED

    
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FlightClass> flightClasses = new ArrayList<>();

    // Getter & Setter
    public List<FlightClass> getFlightClasses() {
        return flightClasses;
    }

    public void setFlightClasses(List<FlightClass> flightClasses) {
        this.flightClasses = flightClasses;
    }

    // Default Constructor
    public AirFlight() {
    }

    // Parameterized Constructor
    public AirFlight(Airline airline, String fnumber, int totalSeat) {
        this.airline = airline;
        this.fnumber = fnumber;
        this.totalSeat = totalSeat;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for airline
    public Airline getAirline() {
        return airline;
    }

    // Setter for airline
    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    // Getter for fnumber
    public String getFnumber() {
        return fnumber;
    }

    // Setter for fnumber
    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    // Getter for totalSeat
    public int getTotalSeat() {
        return totalSeat;
    }

    // Setter for totalSeat
    public void setTotalSeat(int totalSeat) {
        this.totalSeat = totalSeat;
    }
}