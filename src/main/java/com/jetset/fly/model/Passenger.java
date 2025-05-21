package com.jetset.fly.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "passengers") // Using plural for table name convention
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Must be role=2 only (This is a business rule, needs to be enforced in service layer)
    
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private FlightSchedule schedule;
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class flightClass;

    private String pname;

    private String mobileNo;

    private LocalDate dob;

    private String gender;

    @Column(unique = true, nullable = false)
    private String prn; 

    @Column(nullable = false)
    private LocalDateTime bookingAt;

    private String status; // ACTIVE or CANCEL

    // Default Constructor
    public Passenger() {
    }

    // Parameterized Constructor
    public Passenger(Booking booking, User user, Class flightClass, String pname, String mobileNo, LocalDate dob, String gender, String prn, LocalDateTime bookingAt, String status) {
        this.booking = booking;
        this.user = user;
        this.flightClass = flightClass;
        this.pname = pname;
        this.mobileNo = mobileNo;
        this.dob = dob;
        this.gender = gender;
        this.prn = prn;
        this.bookingAt = bookingAt;
        this.status = status;
    }
    public FlightSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(FlightSchedule schedule) {
        this.schedule = schedule;
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

    // Getter for user
    public User getUser() {
        return user;
    }

    // Setter for user
    public void setUser(User user) {
        this.user = user;
    }

    // Getter for flightClass
    public Class getFlightClass() {
        return flightClass;
    }

    // Setter for flightClass
    public void setFlightClass(Class flightClass) {
        this.flightClass = flightClass;
    }

    // Getter for pname
    public String getPname() {
        return pname;
    }

    // Setter for pname
    public void setPname(String pname) {
        this.pname = pname;
    }

    // Getter for mobileNo
    public String getMobileNo() {
        return mobileNo;
    }

    // Setter for mobileNo
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    // Getter for dob
    public LocalDate getDob() {
        return dob;
    }

    // Setter for dob
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    // Getter for gender
    public String getGender() {
        return gender;
    }

    // Setter for gender
    public void setGender(String gender) {
        this.gender = gender;
    }

    // Getter for prn
    public String getPrn() {
        return prn;
    }

    // Setter for prn
    public void setPrn(String prn) {
        this.prn = prn;
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