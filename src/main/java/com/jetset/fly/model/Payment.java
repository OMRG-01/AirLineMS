package com.jetset.fly.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payments") // Using plural for table name convention
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false)
    private Integer noSeat;

    @Column(unique = true, nullable = false)
    private String transactionId; // e.g., UUID or custom generator

    // Redundant but allowed if used frequently for filtering
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private AirFlight flight;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private FlightSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Default Constructor
    public Payment() {
    }

    // Parameterized Constructor
    public Payment(Booking booking, Integer noSeat, String transactionId, AirFlight flight, FlightSchedule schedule, User user) {
        this.booking = booking;
        this.noSeat = noSeat;
        this.transactionId = transactionId;
        this.flight = flight;
        this.schedule = schedule;
        this.user = user;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for booking
    public Booking getBooking() {
        return booking;
    }

    // Setter for booking
    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    // Getter for noSeat
    public Integer getNoSeat() {
        return noSeat;
    }

    // Setter for noSeat
    public void setNoSeat(Integer noSeat) {
        this.noSeat = noSeat;
    }

    // Getter for transactionId
    public String getTransactionId() {
        return transactionId;
    }

    // Setter for transactionId
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    // Getter for user
    public User getUser() {
        return user;
    }

    // Setter for user
    public void setUser(User user) {
        this.user = user;
    }
}