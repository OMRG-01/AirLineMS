package com.jetset.fly.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String aname;


    private int noOfFlight = 0;

    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE or DELETED


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }	


    // Getters and Setters

    public Long getId() {
        return id;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public int getNoOfFlight() {
        return noOfFlight;
    }

    public void setNoOfFlight(int noOfFlight) {
        this.noOfFlight = noOfFlight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
