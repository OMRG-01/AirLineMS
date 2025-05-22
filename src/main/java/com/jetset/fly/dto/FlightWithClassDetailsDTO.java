package com.jetset.fly.dto;

import java.util.List;

import com.jetset.fly.model.FlightSchedule;

public class FlightWithClassDetailsDTO {
    private FlightSchedule schedule; // Assuming you have a FlightSchedule class
    private List<ClassRateDetail> classRates;

    // Default Constructor
    public FlightWithClassDetailsDTO() {
    }

    // Parameterized Constructor
    public FlightWithClassDetailsDTO(FlightSchedule schedule, List<ClassRateDetail> classRates) {
        this.schedule = schedule;
        this.classRates = classRates;
    }

    // Getter for schedule
    public FlightSchedule getSchedule() {
        return schedule;
    }

    // Setter for schedule
    public void setSchedule(FlightSchedule schedule) {
        this.schedule = schedule;
    }

    // Getter for classRates
    public List<ClassRateDetail> getClassRates() {
        return classRates;
    }

    // Setter for classRates
    public void setClassRates(List<ClassRateDetail> classRates) {
        this.classRates = classRates;
    }

    public static class ClassRateDetail {
        private String className;
        private Double rate;
        private int availableSeats;
        private Long classId;
        private Double amount;

        // Default Constructor for ClassRateDetail
        public ClassRateDetail() {
        }

        // Parameterized Constructor for ClassRateDetail
        public ClassRateDetail(String className, Double rate, int availableSeats, Long classId, Double amount) {
            this.className = className;
            this.rate = rate;
            this.availableSeats = availableSeats;
            this.classId = classId;
            this.amount=amount;
        }
        public Double getAmount() {
        	return amount;
        }
        
        public Double setAmount() {
        	return amount;
        }
        // Getter for className
        public String getClassName() {
            return className;
        }

        // Setter for className
        public void setClassName(String className) {
            this.className = className;
        }

        // Getter for rate
        public Double getRate() {
            return rate;
        }

        // Setter for rate
        public void setRate(Double rate) {
            this.rate = rate;
        }

        // Getter for availableSeats
        public int getAvailableSeats() {
            return availableSeats;
        }

        // Setter for availableSeats
        public void setAvailableSeats(int availableSeats) {
            this.availableSeats = availableSeats;
        }

        // Getter for classId
        public Long getClassId() {
            return classId;
        }

        // Setter for classId
        public void setClassId(Long classId) {
            this.classId = classId;
        }
    }
}