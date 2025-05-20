package com.jetset.fly.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings") // It's generally good practice to use plural for table names
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user;

    @ManyToOne
    @JoinColumn(name = "airline_id") 
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private AirFlight flight;

    @ManyToOne
    @JoinColumn(name = "schedule_id") 
    private FlightSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "class_id") 
    private Class flightClass;

    private LocalDateTime bookingAt;

    private String status; // ACTIVE or CANCEL

    // Default Constructor
    public Booking() {
    }

    // Parameterized Constructor
    public Booking(User user, Airline airline, AirFlight flight, FlightSchedule schedule, Class flightClass, LocalDateTime bookingAt, String status) {
        this.user = user;
        this.airline = airline;
        this.flight = flight;
        this.schedule = schedule;
        this.flightClass = flightClass;
        this.bookingAt = bookingAt;
        this.status = status;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for user
    public User getUser() {
        return user;
    }

    // Setter for user
    public void setUser(User user) {
        this.user = user;
    }

    // Getter for airline
    public Airline getAirline() {
        return airline;
    }

    // Setter for airline
    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    // Getter for flight
    public AirFlight getFlight() {
        return flight;
    }

    // Setter for flight
    public void setFlight(AirFlight flight) {
        this.flight = flight;
    }

    // Getter for schedule
    public FlightSchedule getSchedule() {
        return schedule;
    }

    // Setter for schedule
    public void setSchedule(FlightSchedule schedule) {
        this.schedule = schedule;
    }

    // Getter for flightClass
    public Class getFlightClass() {
        return flightClass;
    }

    // Setter for flightClass
    public void setFlightClass(Class flightClass) {
        this.flightClass = flightClass;
    }

    // Getter for bookingAt
    public LocalDateTime getBookingAt() {
        return bookingAt;
    }

    // Setter for bookingAt
    public void setBookingAt(LocalDateTime bookingAt) {
        this.bookingAt = bookingAt;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }
}