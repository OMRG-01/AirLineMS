package com.jetset.fly.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.service.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/flights")
public class FlightScheduleManualController {

    @Autowired
    private AirlineService airlineService;

    @Autowired
    private CityService cityService;

    @Autowired
    private AirFlightService airFlightService;

    @Autowired
    private FlightClassService flightClassService;

    @Autowired
    private FlightScheduleService flightScheduleService;

    @Autowired
    private FlightScheduleRateService flightScheduleRateService;

   

    @GetMapping("/by-airline/{airlineId}")
    @ResponseBody
    public List<Map<String, Object>> getFlightsByAirline(@PathVariable Long airlineId) {
        List<AirFlight> flights = airFlightService.getFlightsByAirline(airlineId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (AirFlight flight : flights) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", flight.getId());
            map.put("fnumber", flight.getFnumber());
            result.add(map);
        }

        return result;
    }


    @GetMapping("/classes/{flightId}")
    @ResponseBody
    public Map<String, Object> getFlightClasses(@PathVariable Long flightId) {
        AirFlight flight = airFlightService.findById(flightId);
        List<FlightClass> flightClasses = flightClassService.getByFlightId(flightId);

        List<Map<String, Object>> classData = new ArrayList<>();
        for (FlightClass fc : flightClasses) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("classId", fc.getFlightClass().getId());
            entry.put("className", fc.getFlightClass().getName());
            entry.put("seat", fc.getSeat());
            classData.add(entry);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("flightNumber", flight.getFnumber());
        response.put("classes", classData);
        return response;
    }

    
}
