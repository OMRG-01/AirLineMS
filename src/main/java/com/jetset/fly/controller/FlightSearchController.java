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
	 
	 private LocalDate parseDate(String dateStr, Model model) {
		    try {
		        return LocalDate.parse(dateStr);
		    } catch (Exception e) {
		        model.addAttribute("error", "Invalid date format.");
		        return null;
		    }
		}
	 private void loadDirectFlights(City sourceCity, City destinationCity, LocalDate selectedDate,
			    Map < Long, List < FlightScheduleRate >> scheduleRatesMap,
			    Map < String, Integer > availableSeatsMap, Model model) {
			    List < FlightSchedule > directFlights = scheduleRepo.findDirectFlightsByDate(sourceCity, destinationCity, "ACTIVE", selectedDate);
			    model.addAttribute("directFlights", directFlights);

			    for (FlightSchedule flight: directFlights) {
			        List < FlightScheduleRate > rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
			        scheduleRatesMap.put(flight.getId(), rates);

			        for (FlightScheduleRate rate: rates) {
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
	 private boolean loadConnectingFlights(City sourceCity, City destinationCity, LocalDate selectedDate,
			    Model model, HttpSession session,
			    Map < Long, List < FlightScheduleRate >> scheduleRatesMap,
			    Map < String, Integer > availableSeatsMap) {
			    List < FlightSchedule > allFlights = scheduleRepo.findByStatus("ACTIVE");
			    Map < City, List < FlightSchedule >> graph = flightPathService.buildGraph(allFlights);
			    List < City > path = flightPathService.findPath(sourceCity, destinationCity, graph);

			    if (path.isEmpty()) return false;

			    List < List < FlightSchedule >> connectingFlights = flightPathService.getConnectingFlights(path, selectedDate);
			    model.addAttribute("multiHopPath", path);
			    model.addAttribute("connectingFlights", connectingFlights);

			    List < Long > connectingFlightIds = new ArrayList < > ();
			    for (List < FlightSchedule > hopFlights: connectingFlights) {
			        for (FlightSchedule flight: hopFlights) {
			            connectingFlightIds.add(flight.getId());
			        }
			    }
			    session.setAttribute("connectingFlightIds", connectingFlightIds);
			    model.addAttribute("connectingFlightIds", connectingFlightIds);

			    // Same rate and seat mapping logic as for direct flights
			    for (List < FlightSchedule > hopFlights: connectingFlights) {
			        for (FlightSchedule flight: hopFlights) {
			            List < FlightScheduleRate > rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
			            scheduleRatesMap.put(flight.getId(), rates);

			            for (FlightScheduleRate rate: rates) {
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

			    Map < String, Object > commonClassInfo = getCommonClassInfo(connectingFlights, scheduleRatesMap, availableSeatsMap);
			    model.addAttribute("commonClassInfo", commonClassInfo);

			    return true;
			}
	 private Map < String, Object > getCommonClassInfo(List < List < FlightSchedule >> connectingFlights,
			    Map < Long, List < FlightScheduleRate >> scheduleRatesMap,
			    Map < String, Integer > availableSeatsMap) {
			    Map < String, Object > commonClassInfo = new HashMap < > ();

			    List < Map < Long, Double >> segmentClassRates = new ArrayList < > ();
			    for (List < FlightSchedule > segment: connectingFlights) {
			        Map < Long, Double > rateMap = new HashMap < > ();
			        for (FlightSchedule flight: segment) {
			            List < FlightScheduleRate > rates = scheduleRatesMap.get(flight.getId());
			            for (FlightScheduleRate rate: rates) {
			                if (rate.getRate() > 0) {
			                    rateMap.put(rate.getFlightClass().getId(),
			                        rateMap.getOrDefault(rate.getFlightClass().getId(), 0.0) + rate.getRate());
			                }
			            }
			        }
			        segmentClassRates.add(rateMap);
			    }

			    Set < Long > commonClassIds = new HashSet < > (segmentClassRates.get(0).keySet());
			    for (Map < Long, Double > segmentMap: segmentClassRates) {
			        commonClassIds.retainAll(segmentMap.keySet());
			    }

			    for (Long classId: commonClassIds) {
			        int totalRate = 0;
			        int minAvailableSeats = Integer.MAX_VALUE;
			        String className = "";

			        for (List < FlightSchedule > segment: connectingFlights) {
			            for (FlightSchedule flight: segment) {
			                List < FlightScheduleRate > rates = scheduleRatesMap.get(flight.getId());
			                for (FlightScheduleRate rate: rates) {
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

			        Map < String, Object > info = new HashMap < > ();
			        info.put("className", className);
			        info.put("totalRate", totalRate);
			        info.put("availableSeats", minAvailableSeats);

			        commonClassInfo.put(classId.toString(), info);
			    }

			    return commonClassInfo;
			}
	 
	 @GetMapping("/user/search-flights")
	 public String searchFlights(@RequestParam Long source,
	                             @RequestParam Long destination,
	                             @RequestParam String date,
	                             @RequestParam Integer passenger,
	                             Model model, HttpSession session) {

	     City sourceCity = cityRepo.findById(source).orElse(null);
	     City destinationCity = cityRepo.findById(destination).orElse(null);
	     LocalDate selectedDate = parseDate(date, model);
	     if (selectedDate == null || sourceCity == null || destinationCity == null) {
	         model.addAttribute("error", "Invalid input.");
	         return "flight-search";
	     }

	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
	     Map<String, Integer> availableSeatsMap = new HashMap<>();

	     List<FlightSchedule> directFlights = scheduleRepo.findDirectFlightsByDate(sourceCity, destinationCity, "ACTIVE", selectedDate);
	     if (!directFlights.isEmpty()) {
	         loadDirectFlights(sourceCity, destinationCity, selectedDate, scheduleRatesMap, availableSeatsMap, model);
	     } else {
	         boolean connectingFound = loadConnectingFlights(sourceCity, destinationCity, selectedDate, model, session, scheduleRatesMap, availableSeatsMap);
	         if (!connectingFound) {
	             model.addAttribute("error", "No flights or connections available.");
	         }
	     }

	     User user = (User) session.getAttribute("loggedInUser");
	     if (user == null) return "redirect:/login1";

	     model.addAttribute("currentUser", user);
	     model.addAttribute("userId", user.getId());
	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("availableSeatsMap", availableSeatsMap);
	     model.addAttribute("from", sourceCity.getCityname());
	     model.addAttribute("to", destinationCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("passengerId", passenger);

	     return "user/flight-search";
	 }
	 @GetMapping("/user/loading2")
	 public String showLoadingUser(@RequestParam Long source,
						             @RequestParam Long destination,
						             @RequestParam String date,
						             @RequestParam Integer passenger,
	                               HttpSession  session,
	                               Model model) {
		 
		 session.setAttribute("from", source);
		    session.setAttribute("to", destination);
		    session.setAttribute("d_date", date);
		    session.setAttribute("nop", passenger);

		    
	     // Fetch city names using city service
	     City fromCity = cityService.getCityById(source);
	     City toCity = cityService.getCityById(destination);

	     model.addAttribute("source", source);
	     model.addAttribute("destination", destination);
	     model.addAttribute("sourceCity", fromCity.getCityname());
	     model.addAttribute("destinationCity", toCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("passenger", passenger);

	     return "user/loading/connectLoading";  // maps to loading.html Thymeleaf template
	 }
	 
	 @GetMapping("/search-flights")
	 public String searchConnectedFlight(@RequestParam Long source,
	                             @RequestParam Long destination,
	                             @RequestParam String date,
	                             @RequestParam Integer passenger,
	                             Model model, HttpSession session) {

	     City sourceCity = cityRepo.findById(source).orElse(null);
	     City destinationCity = cityRepo.findById(destination).orElse(null);
	     LocalDate selectedDate = parseDate(date, model);
	     if (selectedDate == null || sourceCity == null || destinationCity == null) {
	         model.addAttribute("error", "Invalid input.");
	         return "flight-search";
	     }

	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
	     Map<String, Integer> availableSeatsMap = new HashMap<>();

	     List<FlightSchedule> directFlights = scheduleRepo.findDirectFlightsByDate(sourceCity, destinationCity, "ACTIVE", selectedDate);
	     if (!directFlights.isEmpty()) {
	         loadDirectFlights(sourceCity, destinationCity, selectedDate, scheduleRatesMap, availableSeatsMap, model);
	     } else {
	         boolean connectingFound = loadConnectingFlights(sourceCity, destinationCity, selectedDate, model, session, scheduleRatesMap, availableSeatsMap);
	         if (!connectingFound) {
	             model.addAttribute("error", "No flights or connections available.");
	         }
	     }

	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("availableSeatsMap", availableSeatsMap);
	     model.addAttribute("from", sourceCity.getCityname());
	     model.addAttribute("to", destinationCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("passengerId", passenger);

	     return "user/flight-search";
	 }


	 
    
	 



} 
