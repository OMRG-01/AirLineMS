package com.jetset.fly.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jetset.fly.dto.BookingTempRequest;
import com.jetset.fly.dto.PassengerDTO;
import com.jetset.fly.model.User;
import com.jetset.fly.service.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserAPIController {


    @Autowired
    private FlightScheduleService flightScheduleService;

    @GetMapping("/available-dates")
    @ResponseBody
    public List<LocalDate> getAvailableDates(@RequestParam int from, @RequestParam int to) {
        return flightScheduleService.findAvailableDatesBetweenCities(from, to);
    }



    @PostMapping("/temp-save-connected")
    public ResponseEntity<String> saveTempPassengerData(
            @RequestBody List<PassengerDTO> passengers,
            HttpSession session) {

        // Save passengers temporarily in session
        session.setAttribute("tempPassengers", passengers);

        // Return success
        return ResponseEntity.ok("Passenger details saved temporarily.");
    }
    // STEP 3: Show payment page
    
}
