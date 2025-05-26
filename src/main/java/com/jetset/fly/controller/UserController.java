package com.jetset.fly.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jetset.fly.dto.BookingDTO;
import com.jetset.fly.dto.BookingTempRequest;
import com.jetset.fly.dto.PassengerDTO;
import com.jetset.fly.model.*;

import com.jetset.fly.model.Class;
import com.jetset.fly.service.*;
import com.jetset.fly.repository.*;

import jakarta.servlet.http.HttpSession;


@Controller
public class UserController {

    @Autowired
    private FlightScheduleService flightScheduleService;
    
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
	 
	 @GetMapping("/otp-view")
	 public String viewOtpPage(Model model) {
	     model.addAttribute("otp", "123456"); // example OTP
	     return "email/reviewEmail"; // path inside templates folder
	 }

    
    @GetMapping("/user/fair-flight")
	 public String searchFlightsUser(@RequestParam(required = false) Long fromCityId,
	                             @RequestParam(required = false) Long toCityId,
	                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                             @RequestParam(required = false) Integer  passengers,
	                             Model model, HttpSession session) {
		 if (fromCityId == null) fromCityId = (Long) session.getAttribute("from");
		    if (toCityId == null) toCityId = (Long) session.getAttribute("to");
		    if (date == null) date = LocalDate.parse((String) session.getAttribute("d_date"));
		    if (passengers == null) passengers = (Integer) session.getAttribute("nop");
		    
	     List<FlightSchedule> flights = flightScheduleService.findFlightsForRouteAndDate(fromCityId, toCityId, date);

	     // Map<scheduleId, List<FlightScheduleRate>>
	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();

	     // Map<scheduleId-classId, availableSeatCount>
	     Map<String, Integer> availableSeatsMap = new HashMap<>();

	     for (FlightSchedule flight : flights) {
	    	    List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
	    	    scheduleRatesMap.put(flight.getId(), rates);

	    	    for (FlightScheduleRate rate : rates) {
	    	        Long flightId = rate.getFlight().getId();
	    	        Long classId = rate.getFlightClass().getId();

	    	        // Fetch seat capacity from FlightClass using flight + class
	    	        FlightClass flightClass = flightClassService.findByFlightIdAndClassId(flightId, classId);
	    	        int totalCapacity = flightClass.getSeat();

	    	        int bookedSeats = passengerService.countPassengersByScheduleAndClass(flight.getId(), classId);

	    	        int availableSeats = totalCapacity - bookedSeats;
	    	        String key = flight.getId() + "-" + classId;

	    	        availableSeatsMap.put(key, availableSeats);
	    	    }
	    	}


	     City fromCity = cityService.getCityById(fromCityId);
	     City toCity = cityService.getCityById(toCityId);
	     User user = (User) session.getAttribute("loggedInUser");
	     if (user != null) {
	         model.addAttribute("currentUser", user);
	         model.addAttribute("userId", user.getId()); // if needed on frontend
	     }else if (user==null) {
	    	 return "redirect:/login1";
	     }

	     model.addAttribute("flights", flights);
	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("availableSeatsMap", availableSeatsMap); // 🆕
	     model.addAttribute("from", fromCity.getCityname());
	     model.addAttribute("to", toCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("passengers", passengers);
	     return "user/userFlightBooking";
	 }

	 
	


	 
	 @GetMapping("/user/loading")
	 public String showLoadingUser(@RequestParam Long from,
	                               @RequestParam Long to,
	                               @RequestParam String d_date,
	                               @RequestParam int nop,
	                               HttpSession  session,
	                               Model model) {
		 
		 session.setAttribute("from", from);
		    session.setAttribute("to", to);
		    session.setAttribute("d_date", d_date);
		    session.setAttribute("nop", nop);

		    
	     // Fetch city names using city service
	     City fromCity = cityService.getCityById(from);
	     City toCity = cityService.getCityById(to);

	     model.addAttribute("from", from);
	     model.addAttribute("to", to);
	     model.addAttribute("fromCity", fromCity.getCityname());
	     model.addAttribute("toCity", toCity.getCityname());
	     model.addAttribute("d_date", d_date);
	     model.addAttribute("nop", nop);

	     return "user/bookingloading";  // maps to loading.html Thymeleaf template
	 }
	 
	 @GetMapping("/user/flightstatus")
	 public String searchFlightsUserStatus(@RequestParam("from") Long fromCityId,
	                             @RequestParam("to") Long toCityId,
	                             @RequestParam("d_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                            
	                             Model model) {

	     List<FlightSchedule> flights = flightScheduleService.findFlightsStatus(fromCityId, toCityId, date);
	     
	     
	     // Prepare a map of FlightSchedule ID -> List of FlightScheduleRate
	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
	     for (FlightSchedule flight : flights) {
	         List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
	         scheduleRatesMap.put(flight.getId(), rates);
	     }
	     
	     City fromCity = cityService.getCityById(fromCityId); // Replace with your actual method
	     City toCity = cityService.getCityById(toCityId);

	     model.addAttribute("flights", flights);
	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("from", fromCity.getCityname());
	     model.addAttribute("to", toCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("now", LocalDateTime.now());

	     return "user/userFairStatus";
	 }
	 
	 @GetMapping("/user/bookings")
	 public String getUserBookings(HttpSession session, Model model) {
	     User loggedInUser = (User) session.getAttribute("loggedInUser");
	    
	     if (loggedInUser == null) {
	         return "redirect:/login1"; // Or your login page route
	     }
	     
	     List<Booking> bookings = bookingRepository.findByUserId(loggedInUser.getId());
	     model.addAttribute("bookings", bookings);
	     model.addAttribute("now", LocalDateTime.now());

	     return "user/myBooking"; // your myBooking.html
	 }
	 
	 @Autowired
	 private BookingService bookingService;
	 
	 
	 @GetMapping("/booking/passengerView")
	 public String viewPassengers(@RequestParam("bookingId") Long bookingId, Model model) {
	     Booking booking = bookingService.findById(bookingId);
	     List<Passenger> passengers = passengerService.findByBookingId(bookingId);

	     model.addAttribute("booking", booking);
	     model.addAttribute("passengers", passengers);

	     return "user/passengerView";
	 }
	 // GET: Show Profile Edit Page
	 @GetMapping("/user/profileEdit")
	 public String showEditProfilePage(Model model, HttpSession session) {
	     User loggedInUser = (User) session.getAttribute("loggedInUser");
	     if (loggedInUser == null) {
	         return "redirect:/login1";
	     }

	     User user = userService.findByEmail(loggedInUser.getEmail());
	     model.addAttribute("user", user);
	     return "user/profileEdit";  // View should be at templates/user/profileEdit.html
	 }


	    // POST: Save Profile Changes
	    @PostMapping("/user/update")
	    public String updateProfile(@ModelAttribute("user") User updatedUser, HttpSession session) {
	        User existingUser = (User) session.getAttribute("loggedInUser");

	        if (existingUser == null) {
	            return "redirect:/login1"; // Not logged in
	        }

	        // Only update editable fields
	        existingUser.setTitle(updatedUser.getTitle());
	        existingUser.setName(updatedUser.getName());
	        existingUser.setMobile(updatedUser.getMobile());
	        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
	        existingUser.setGender(updatedUser.getGender());
	        existingUser.setStreetAddress(updatedUser.getStreetAddress());
	        existingUser.setCity(updatedUser.getCity());
	        existingUser.setState(updatedUser.getState());
	        existingUser.setZipCode(updatedUser.getZipCode());
	        existingUser.setCountry(updatedUser.getCountry());
	        existingUser.setTravelPreference(updatedUser.getTravelPreference());
	        existingUser.setPreferredAirline(updatedUser.getPreferredAirline());

	        userService.save(existingUser);

	        // Refresh session user
	        session.setAttribute("loggedInUser", existingUser);

	        return "redirect:/user/profile/edit?success";
	    }


}
