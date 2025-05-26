package com.jetset.fly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jetset.fly.model.*;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;

import jakarta.servlet.http.HttpSession;

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
    
    
    @Autowired
    private FlightScheduleRateService flightScheduleRateService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
	 private FlightClassService flightClassService;
	 
	 @Autowired
	 private PassengerService passengerService;
	 
	 @Autowired
	 private CityService cityService;
	 @GetMapping("/search-flights")
	 public String searchFlights(@RequestParam Long source,
	                             @RequestParam Long destination,
	                             @RequestParam String date,
	                             @RequestParam Integer passenger,
	                             Model model, HttpSession session) {

	     City sourceCity = cityRepo.findById(source).orElse(null);
	     City destinationCity = cityRepo.findById(destination).orElse(null);
	     LocalDate selectedDate;

	     try {
	         selectedDate = LocalDate.parse(date);
	     } catch (Exception e) {
	         model.addAttribute("error", "Invalid date format.");
	         return "flight-search";
	     }

	     if (sourceCity == null || destinationCity == null) {
	         model.addAttribute("error", "Invalid source or destination.");
	         return "flight-search";
	     }

	     // ✅ Map to hold rate info and available seats
	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
	     Map<String, Integer> availableSeatsMap = new HashMap<>();

	     // ✅ Find direct flights
	     List<FlightSchedule> directFlights = scheduleRepo.findDirectFlightsByDate(
	         sourceCity, destinationCity, "ACTIVE", selectedDate
	     );

	     if (!directFlights.isEmpty()) {
	         model.addAttribute("directFlights", directFlights);

	         // Fill scheduleRatesMap & availableSeatsMap for direct flights
	         for (FlightSchedule flight : directFlights) {
	             List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
	             scheduleRatesMap.put(flight.getId(), rates);

	             for (FlightScheduleRate rate : rates) {
	                 Long flightId = rate.getFlight().getId();
	                 Long classId = rate.getFlightClass().getId();

	                 FlightClass flightClass = flightClassService.findByFlightIdAndClassId(flightId, classId);
	                 int totalCapacity = flightClass.getSeat();
	                 int bookedSeats = passengerService.countPassengersByScheduleAndClass(flight.getId(), classId);
	                 int availableSeats = totalCapacity - bookedSeats;

	                 String key = flight.getId() + "-" + classId;
	                 availableSeatsMap.put(key, availableSeats);
	             }
	         }
	     } else {
	         // 🔁 Build Graph & Path for Connecting Flights
	         List<FlightSchedule> allFlights = scheduleRepo.findByStatus("ACTIVE");
	         Map<City, List<FlightSchedule>> graph = flightPathService.buildGraph(allFlights);
	         List<City> path = flightPathService.findPath(sourceCity, destinationCity, graph);

	         if (!path.isEmpty()) {
	             List<List<FlightSchedule>> connectingFlights = flightPathService.getConnectingFlights(path, selectedDate);
	             boolean hasAnyValidFlight = connectingFlights.stream().anyMatch(list -> !list.isEmpty());

	             if (hasAnyValidFlight) {
	                 model.addAttribute("multiHopPath", path);
	                 model.addAttribute("connectingFlights", connectingFlights);
	                 	
	                 List<Long> connectingFlightIds = new ArrayList<>();
	                 for (List<FlightSchedule> hopFlights : connectingFlights) {
	                     for (FlightSchedule flight : hopFlights) {
	                         connectingFlightIds.add(flight.getId());
	                     }
	                 }
	                 // Save flight IDs in session
	                 session.setAttribute("connectingFlightIds", connectingFlightIds);
	                 
	                 model.addAttribute("connectingFlightIds", connectingFlightIds);
	                 // Map classes and seats for connecting flights as well (but no booking shown)
	                 for (List<FlightSchedule> hopFlights : connectingFlights) {
	                     for (FlightSchedule flight : hopFlights) {
	                         List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
	                         scheduleRatesMap.put(flight.getId(), rates);

	                         for (FlightScheduleRate rate : rates) {
	                             Long flightId = rate.getFlight().getId();
	                             Long classId = rate.getFlightClass().getId();

	                             FlightClass flightClass = flightClassService.findByFlightIdAndClassId(flightId, classId);
	                             int totalCapacity = flightClass.getSeat();
	                             int bookedSeats = passengerService.countPassengersByScheduleAndClass(flight.getId(), classId);
	                             int availableSeats = totalCapacity - bookedSeats;

	                             String key = flight.getId() + "-" + classId;
	                             availableSeatsMap.put(key, availableSeats);
	                         }
	                     }
	                 }
	                 // ✅ Final Output: Common Class → [Total Rate, Min Available Seats]
	                 Map<String, Object> commonClassInfo = new HashMap<>();

	                 if (connectingFlights != null && !connectingFlights.isEmpty()) {
	                     // Step 1: Build classId → rate map for each flight segment
	                     List<Map<Long, Double>> segmentClassRates = new ArrayList<>();

	                     for (List<FlightSchedule> segment : connectingFlights) {
	                         Map<Long, Double> rateMap = new HashMap<>();
	                         for (FlightSchedule flight : segment) {
	                             List<FlightScheduleRate> rates = scheduleRatesMap.get(flight.getId());
	                             for (FlightScheduleRate rate : rates) {
	                                 if (rate.getRate() > 0) {
	                                     rateMap.put(rate.getFlightClass().getId(),
	                                         rateMap.getOrDefault(rate.getFlightClass().getId(), 0.0) + rate.getRate());
	                                 }
	                             }
	                         }
	                         segmentClassRates.add(rateMap);
	                     }

	                     // Step 2: Find common class IDs
	                     Set<Long> commonClassIds = new HashSet<>(segmentClassRates.get(0).keySet());
	                     for (Map<Long, Double> segmentMap : segmentClassRates) {
	                         commonClassIds.retainAll(segmentMap.keySet());
	                     }

	                     // Step 3: For each common class, calculate total rate and minimum available seats
	                     for (Long classId : commonClassIds) {
	                         int totalRate = 0;
	                         int minAvailableSeats = Integer.MAX_VALUE;
	                         String className = "";

	                         for (List<FlightSchedule> segment : connectingFlights) {
	                             for (FlightSchedule flight : segment) {
	                                 List<FlightScheduleRate> rates = scheduleRatesMap.get(flight.getId());
	                                 for (FlightScheduleRate rate : rates) {
	                                     if (rate.getFlightClass().getId().equals(classId)) {
	                                         totalRate += rate.getRate();
	                                         className = rate.getFlightClass().getName();

	                                         String key = flight.getId() + "-" + classId;
	                                         int seats = availableSeatsMap.getOrDefault(key, 0);
	                                         minAvailableSeats = Math.min(minAvailableSeats, seats);
	                                     }
	                                 }
	                             }
	                         }

	                         // Final record
	                         Map<String, Object> info = new HashMap<>();
	                         info.put("className", className);
	                         info.put("totalRate", totalRate);
	                         info.put("availableSeats", minAvailableSeats);

	                         commonClassInfo.put(classId.toString(), info);
	                     }

	                     model.addAttribute("commonClassInfo", commonClassInfo);
	                 }

	             } else {
	                 model.addAttribute("noConnectingFlights", true);
	             }
	         } else {
	             model.addAttribute("error", "No flights or connections available.");
	         }
	     }

	     User user = (User) session.getAttribute("loggedInUser");
	     if (user != null) {
	         model.addAttribute("currentUser", user);
	         model.addAttribute("userId", user.getId());
	     } else {
	         return "redirect:/login1";
	     }

	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("availableSeatsMap", availableSeatsMap);
	     model.addAttribute("from", sourceCity.getCityname());
	     model.addAttribute("to", destinationCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("passengerId", passenger); // used on frontend

	     return "user/flight-search";
	 }

    
    
//	 @GetMapping("/search-flights")
//	 public String searchFlights(@RequestParam Long source,
//	                              @RequestParam Long destination,
//	                              @RequestParam String date,
//	                              @RequestParam Integer passenger,
//	                              Model model, HttpSession session) {
//
//	     City sourceCity = cityRepo.findById(source).orElse(null);
//	     City destinationCity = cityRepo.findById(destination).orElse(null);
//	     LocalDate selectedDate;
//
//	     try {
//	         selectedDate = LocalDate.parse(date);
//	     } catch (Exception e) {
//	         model.addAttribute("error", "Invalid date format.");
//	         return "flight-search";
//	     }
//
//	     if (sourceCity == null || destinationCity == null) {
//	         model.addAttribute("error", "Invalid source or destination.");
//	         return "flight-search";
//	     }
//
//	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
//	     Map<String, Integer> availableSeatsMap = new HashMap<>();
//
//	     // 🔹 Direct Flights
//	     List<FlightSchedule> directFlights = scheduleRepo.findDirectFlightsByDate(
//	         sourceCity, destinationCity, "ACTIVE", selectedDate
//	     );
//
//	     if (!directFlights.isEmpty()) {
//	         model.addAttribute("directFlights", directFlights);
//
//	         // Fill scheduleRatesMap & availableSeatsMap for direct flights
//           for (FlightSchedule flight : directFlights) {
//               List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
//               scheduleRatesMap.put(flight.getId(), rates);
//
//               for (FlightScheduleRate rate : rates) {
//                   Long flightId = rate.getFlight().getId();
//                   Long classId = rate.getFlightClass().getId();
//
//                   FlightClass flightClass = flightClassService.findByFlightIdAndClassId(flightId, classId);
//                   int totalCapacity = flightClass.getSeat();
//                   int bookedSeats = passengerService.countPassengersByScheduleAndClass(flight.getId(), classId);
//                   int availableSeats = totalCapacity - bookedSeats;
//
//                   String key = flight.getId() + "-" + classId;
//                   availableSeatsMap.put(key, availableSeats);
//               }
//           }
//	     } else {
//	         // 🔁 Connecting Flights Logic
//	         List<FlightSchedule> allFlights = scheduleRepo.findByStatus("ACTIVE");
//	         Map<City, List<FlightSchedule>> graph = flightPathService.buildGraph(allFlights);
//	         List<City> path = flightPathService.findPath(sourceCity, destinationCity, graph);
//
//	         if (!path.isEmpty()) {
//	             List<List<FlightSchedule>> connectingFlights = flightPathService.getConnectingFlights(path, selectedDate);
//	             boolean hasAnyValidFlight = connectingFlights.stream().anyMatch(list -> !list.isEmpty());
//
//	             if (hasAnyValidFlight) {
//	            	    model.addAttribute("multiHopPath", path);
//	            	    model.addAttribute("connectingFlights", connectingFlights);
//
//	            	    // 1. Collect all classes per hop
//	            	    List<Set<Long>> classIdsPerHop = new ArrayList<>();
//	            	    Map<Long, String> classNames = new HashMap<>();
//
//	            	 
//
//	            	    for (List<FlightSchedule> hopFlights : connectingFlights) {
//	            	        Set<Long> classIdsInThisHop = new HashSet<>();
//
//	            	        for (FlightSchedule flight : hopFlights) {
//	            	            List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
//	            	            scheduleRatesMap.put(flight.getId(), rates);
//
//	            	            for (FlightScheduleRate rate : rates) {
//	            	                Long classId = rate.getFlightClass().getId();
//	            	                classIdsInThisHop.add(classId);
//	            	                classNames.putIfAbsent(classId, rate.getFlightClass().getName());
//
//	            	                // Calculate available seats
//	            	                FlightClass flightClass = flightClassService.findByFlightIdAndClassId(
//	            	                    rate.getFlight().getId(), classId);
//	            	                int totalCapacity = flightClass.getSeat();
//	            	                int bookedSeats = passengerService.countPassengersByScheduleAndClass(
//	            	                    rate.getFlight().getId(), classId);
//	            	                int availableSeats = totalCapacity - bookedSeats;
//
//	            	                String key = rate.getFlight().getId() + "-" + classId;
//	            	                availableSeatsMap.put(key, availableSeats);
//	            	            }
//	            	        }
//	            	        classIdsPerHop.add(classIdsInThisHop);
//	            	    }
//
//	            	    // 2. Find intersection (common classes) across all hops
//	            	    Set<Long> commonClassIds = new HashSet<>(classIdsPerHop.get(0));
//	            	    for (int i = 1; i < classIdsPerHop.size(); i++) {
//	            	        commonClassIds.retainAll(classIdsPerHop.get(i));
//	            	    }
//
//	            	    // 3. Calculate total rates & minimum available seats for these common classes
//	            	    Map<Long, Double> totalRates = new HashMap<>();
//	            	    Map<Long, Integer> minAvailableSeats = new HashMap<>();
//
//	            	    for (Long classId : commonClassIds) {
//	            	        double sumRate = 0.0;
//	            	        int minSeats = Integer.MAX_VALUE;
//
//	            	        // For each hop, sum rate and find min available seats
//	            	        for (List<FlightSchedule> hopFlights : connectingFlights) {
//	            	            // Sum over all flights in this hop for the classId
//	            	            for (FlightSchedule flight : hopFlights) {
//	            	                List<FlightScheduleRate> rates = scheduleRatesMap.get(flight.getId());
//	            	                if (rates == null) continue;
//
//	            	                for (FlightScheduleRate rate : rates) {
//	            	                    if (rate.getFlightClass().getId().equals(classId)) {
//	            	                        sumRate += rate.getRate();
//
//	            	                        String key = flight.getId() + "-" + classId;
//	            	                        int seats = availableSeatsMap.getOrDefault(key, 0);
//	            	                        if (seats < minSeats) minSeats = seats;
//	            	                    }
//	            	                }
//	            	            }
//	            	        }
//
//	            	        totalRates.put(classId, sumRate);
//	            	        minAvailableSeats.put(classId, minSeats);
//	            	    }
//
//	            	    // 4. Add to model for frontend
//	            	    model.addAttribute("commonClassIds", commonClassIds);
//	            	    model.addAttribute("classNames", classNames);
//	            	    model.addAttribute("classTotalRates", totalRates);
//	            	    model.addAttribute("classAvailableSeats", minAvailableSeats);
//
//	            	    // Also add scheduleRatesMap & availableSeatsMap if needed elsewhere
//	            	    model.addAttribute("scheduleRatesMap", scheduleRatesMap);
//	            	    model.addAttribute("availableSeatsMap", availableSeatsMap);
//	            	} else {
//	                 model.addAttribute("noConnectingFlights", true);
//	             }
//	         } else {
//	             model.addAttribute("error", "No flights or connections available.");
//	         }
//	     }
//
//	     // 🔐 User session check
//	     User user = (User) session.getAttribute("loggedInUser");
//	     if (user != null) {
//	         model.addAttribute("currentUser", user);
//	         model.addAttribute("userId", user.getId());
//	     } else {
//	         return "redirect:/login1";
//	     }
//
//	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
//	     model.addAttribute("availableSeatsMap", availableSeatsMap);
//	     model.addAttribute("from", sourceCity.getCityname());
//	     model.addAttribute("to", destinationCity.getCityname());
//	     model.addAttribute("date", date);
//	     model.addAttribute("passengerId", passenger);
//
//	     return "user/flight-search";
//	 }






    @GetMapping("/search-form")
    public String searchForm(Model model) {
        model.addAttribute("cities", cityRepo.findByStatus("ACTIVE"));
        return "user/search-form";
    }
} 
