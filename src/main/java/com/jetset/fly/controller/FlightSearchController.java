package com.jetset.fly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jetset.fly.model.*;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;

import java.time.LocalDate;
import java.util.*;
@Controller
public class FlightSearchController {

    @Autowired
    private CityRepository cityRepo;

    @Autowired
    private FlightScheduleRepository scheduleRepo;

    @Autowired
    private FlightPathService flightPathService;

//    @GetMapping("/search-flights")
//    public String searchFlights(@RequestParam String source,
//                                 @RequestParam String destination,
//                                 @RequestParam String date,
//                                 Model model) {
//
//        City sourceCity = cityRepo.findByCityname(source).orElse(null);
//        City destinationCity = cityRepo.findByCityname(destination).orElse(null);
//        LocalDate selectedDate = LocalDate.parse(date);
//        
//        if (sourceCity == null || destinationCity == null) {
//            model.addAttribute("error", "Invalid source or destination.");
//            return "flight-search";
//        }
//
//        List<FlightSchedule> directFlights = scheduleRepo.findDirectFlightsByDate(
//        	    sourceCity, destinationCity, "ACTIVE", selectedDate // LocalDate type
//        	);
//
//
//        if (!directFlights.isEmpty()) {
//        	model.addAttribute("directFlights",directFlights);
//        } else {
//            List<FlightSchedule> allFlights = scheduleRepo.findByStatus("ACTIVE");
//            Map<City, List<FlightSchedule>> graph = flightPathService.buildGraph(allFlights);
//
//            List<City> path = flightPathService.findPath(sourceCity, destinationCity, graph);
//
//            if (!path.isEmpty()) {
//                List<List<FlightSchedule>> connectingFlights = flightPathService.getConnectingFlights(path,selectedDate);
//
//                // ✅ Check if we got any valid connecting flight options
//                boolean hasAnyValidFlight = connectingFlights.stream().anyMatch(list -> !list.isEmpty());
//
//                if (hasAnyValidFlight) {
//                    model.addAttribute("multiHopPath", path);
//                    model.addAttribute("connectingFlights", connectingFlights);
//                } else {
//                    model.addAttribute("noConnectingFlights", true); // ❌ No valid connections
//                }
//            } else {
//                model.addAttribute("error", "No flights or connections available.");
//            }
//        }
//        
//        return "user/flight-search";
//    }
    @GetMapping("/search-flights")
    public String searchFlights(@RequestParam String source,
                                @RequestParam String destination,
                                @RequestParam String date,
                                Model model) {

        City sourceCity = cityRepo.findByCityname(source).orElse(null);
        City destinationCity = cityRepo.findByCityname(destination).orElse(null);
        LocalDate selectedDate = LocalDate.parse(date);

        if (sourceCity == null || destinationCity == null) {
            model.addAttribute("error", "Invalid source or destination.");
            return "flight-search";
        }

        List<FlightSchedule> directFlights = scheduleRepo.findDirectFlightsByDate(
                sourceCity, destinationCity, "ACTIVE", selectedDate);

        Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
        Map<String, Integer> availableSeatsMap = new HashMap<>();

        // 🔹 Handle Direct Flights
        for (FlightSchedule flight : directFlights) {
            addRatesAndSeats(flight, scheduleRatesMap, availableSeatsMap);
        }

        if (!directFlights.isEmpty()) {
            model.addAttribute("directFlights", directFlights);
        } else {
            List<FlightSchedule> allFlights = scheduleRepo.findByStatus("ACTIVE");
            Map<City, List<FlightSchedule>> graph = flightPathService.buildGraph(allFlights);

            List<City> path = flightPathService.findPath(sourceCity, destinationCity, graph);

            if (!path.isEmpty()) {
                List<List<FlightSchedule>> connectingFlights = flightPathService.getConnectingFlights(path, selectedDate);

                boolean hasValidFlight = connectingFlights.stream().anyMatch(list -> !list.isEmpty());

                if (hasValidFlight) {
                    // 🔹 Handle Connecting Flights
                    for (List<FlightSchedule> segment : connectingFlights) {
                        for (FlightSchedule flight : segment) {
                            addRatesAndSeats(flight, scheduleRatesMap, availableSeatsMap);
                        }
                    }

                    model.addAttribute("multiHopPath", path);
                    model.addAttribute("connectingFlights", connectingFlights);
                } else {
                    model.addAttribute("noConnectingFlights", true);
                }
            } else {
                model.addAttribute("error", "No flights or connections available.");
            }
        }

        model.addAttribute("scheduleRatesMap", scheduleRatesMap);
        model.addAttribute("availableSeatsMap", availableSeatsMap);

        return "user/flight-search";
    }

    @Autowired
    private FlightScheduleRateService flightScheduleRateService;

    @Autowired
	 private FlightClassService flightClassService;

	 @Autowired
	 private PassengerService passengerService;
    // 🔧 Utility Method
    private void addRatesAndSeats(FlightSchedule flight,
                                   Map<Long, List<FlightScheduleRate>> scheduleRatesMap,
                                   Map<String, Integer> availableSeatsMap) {

        List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
        scheduleRatesMap.put(flight.getId(), rates);

        for (FlightScheduleRate rate : rates) {
            Long flightId = rate.getFlight().getId();
            Long classId = rate.getFlightClass().getId();

            FlightClass flightClass = flightClassService.findByFlightIdAndClassId(flightId, classId);
            int totalCapacity = flightClass.getSeat();

            int bookedSeats = passengerService.countPassengersByScheduleAndClass(flightId, classId);
            int availableSeats = totalCapacity - bookedSeats;

            String key = flightId + "-" + classId;
            availableSeatsMap.put(key, availableSeats);
        }
    }


    @GetMapping("/search-form")
    public String searchForm(Model model) {
        model.addAttribute("cities", cityRepo.findByStatus("ACTIVE"));
        return "user/search-form";
    }
} 
