package com.jetset.fly.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jetset.fly.service.*;

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



    
}
