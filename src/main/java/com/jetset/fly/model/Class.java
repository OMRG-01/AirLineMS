package com.jetset.fly.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "class")
public class Class  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Business, Economy, First Class

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE or DELETED
    
    @PrePersist
    protected void onCreate() {
        this.status = "ACTIVE";
    }
    public Class() {}

	public Class(Long id) {
	    this.id = id;
	}

    // Parameterized Constructor
    public Class(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }
}