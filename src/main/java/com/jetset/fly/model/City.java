package com.jetset.fly.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cityname;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE or DELETED

    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return this.cityname;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public City() {
   
    }
    
    public City(Long id) {
        this.id = id;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
