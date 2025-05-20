package com.jetset.fly.dto;
import java.time.LocalDate;

public class PassengerDTO {
    private Long userId;
    private String pname;
    private String mobileNo;
    private LocalDate dob;
    private String gender;

    // Default Constructor
    public PassengerDTO() {
    }

    // Parameterized Constructor
    public PassengerDTO(Long userId, String pname, String mobileNo, LocalDate dob, String gender) {
        this.userId = userId;
        this.pname = pname;
        this.mobileNo = mobileNo;
        this.dob = dob;
        this.gender = gender;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getPname() {
        return pname;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}