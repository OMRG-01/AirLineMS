package com.jetset.fly.model;
import jakarta.persistence.*;

@Entity
@Table(name = "flight_class")
public class FlightClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private AirFlight flight;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class flightClass;

    @Column(nullable = false)
    private int seat;

    // Default Constructor
    public FlightClass() {
    }

    // Parameterized Constructor
    public FlightClass(AirFlight flight, Class flightClass, int seat) {
        this.flight = flight;
        this.flightClass = flightClass;
        this.seat = seat;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for flight
    public AirFlight getFlight() {
        return flight;
    }

    // Setter for flight
    public void setFlight(AirFlight flight) {
        this.flight = flight;
    }

    // Getter for flightClass (renamed to avoid naming conflict with the class name)
    public Class getFlightClass() {
        return flightClass;
    }

    // Setter for flightClass (renamed to avoid naming conflict with the class name)
    public void setFlightClass(Class flightClass) {
        this.flightClass = flightClass;
    }

    // Getter for seat
    public int getSeat() {
        return seat;
    }

    // Setter for seat
    public void setSeat(int seat) {
        this.seat = seat;
    }
}