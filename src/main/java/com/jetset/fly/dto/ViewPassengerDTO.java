package com.jetset.fly.dto;

import com.jetset.fly.model.Passenger;

public class ViewPassengerDTO {
    private String pname;
    private String prn;
    private String mobileNo;
    private String gender;
    private String bookingSummary;

    public ViewPassengerDTO(Passenger p) {
        this.pname = p.getPname();
        this.prn = p.getPrn();
        this.mobileNo = p.getMobileNo();
        this.gender = p.getGender();

        String flightNumber = p.getSchedule().getFlight().getFnumber(); // e.g., FL745
        String sourceCode = extractCityCode(p.getSchedule().getSource().getCityname()); // e.g., NSK
        String destCode = extractCityCode(p.getSchedule().getDestination().getCityname()); // e.g., MOB

        this.bookingSummary = flightNumber + ":" + sourceCode + "->" + destCode;
    }

    private String extractCityCode(String cityName) {
        // Assumes format "Nashik(NSK)" → extract between parentheses
        if (cityName != null && cityName.contains("(") && cityName.contains(")")) {
            return cityName.substring(cityName.indexOf("(") + 1, cityName.indexOf(")"));
        }
        return cityName != null ? cityName : "N/A";
    }

    // Getters
    public String getPname() { return pname; }
    public String getPrn() { return prn; }
    public String getMobileNo() { return mobileNo; }
    public String getGender() { return gender; }
    public String getBookingSummary() { return bookingSummary; }
}
