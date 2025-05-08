package com.jetset.fly.model;

import jakarta.persistence.*;

@Entity
@Table(name = "air_flight")
public class AirFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @Column(nullable = false, unique = true)
    private String fnumber;

    @Column(nullable = false)
    private int totalSeat;

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