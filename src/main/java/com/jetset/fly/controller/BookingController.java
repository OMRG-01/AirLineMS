package com.jetset.fly.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
public class BookingController {

    @Autowired
    private UserService userService;

    @Autowired
    private FlightScheduleService flightScheduleService;

    @Autowired
    private AirFlightService airFlightService;

    @Autowired
    private AirlineService airlineService;

    @Autowired
    private ClassService classService;
    
    

    /**
     * Handle booking initiation from fare selection page.
     */
    
    @PostMapping("/booking/initiate")
    public String initiateBooking(@RequestParam Long userId,
                                  @RequestParam Long flightId,
                                  @RequestParam Long airlineId,
                                  @RequestParam Long scheduleId,
                                  @RequestParam Long flightClassId,
                                  
                                  @RequestParam Integer nos_passanger,
                                  @RequestParam Double rate,
                                  Model model,
                                  HttpSession session) {

        // ✅ Validate that user, flight, schedule, airline, and class exist
        User user = userService.getById(userId);
        AirFlight flight = airFlightService.findById(flightId);
        Airline airline = airlineService.getById(airlineId);
        FlightSchedule schedule = flightScheduleService.findById(scheduleId);
        Class flightClass = classService.findById(flightClassId);

        // ✅ Store in session or pass to model (your choice)
        session.setAttribute("bookingUser", user);
        session.setAttribute("bookingFlight", flight);
        session.setAttribute("bookingAirline", airline);
        session.setAttribute("bookingSchedule", schedule);
        session.setAttribute("bookingClass", flightClass);

        // OR use model attributes (if showing on next page):
        model.addAttribute("user", user);
        model.addAttribute("flight", flight);
        model.addAttribute("airline", airline);
        model.addAttribute("schedule", schedule);
        model.addAttribute("flightClass", flightClass);
        model.addAttribute("nos_passanger", nos_passanger);
        model.addAttribute("rate", rate);

        // ✅ Redirect to booking detail or passenger form
        return "user/bookingDetails";  // <- You need to create this view next
    }
    
    @PostMapping("/booking/temp-save")
    @ResponseBody
    public ResponseEntity<String> tempBookingSave(@RequestBody BookingTempRequest data, HttpSession session) {
        // Save to session
    	User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setAirlineId(data.getAirlineId());
        bookingDTO.setFlightId(data.getFlightId());
        bookingDTO.setScheduleId(data.getScheduleId());
        bookingDTO.setFlightClassId(data.getFlightClassId());
        bookingDTO.setUserId(data.getUserId());
 // fetch from session

        for (PassengerDTO dto : data.getPassengers()) {
            dto.setUserId(user.getId());
        }
        
        session.setAttribute("bookingDTO", bookingDTO);
        session.setAttribute("passengers", data.getPassengers());
        session.setAttribute("rate", data.getRate());
        session.setAttribute("noOfPassengers", data.getNoOfPassengers());

        return ResponseEntity.ok("Saved");
    }
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @GetMapping("/booking/gateway")
    public String showGatewayPage(HttpSession session, Model model) {
        BookingDTO bookingDTO = (BookingDTO) session.getAttribute("bookingDTO");
        if (bookingDTO == null) {
            return "redirect:/bookingDetails"; 
        }

        List<PassengerDTO> passengers = (List<PassengerDTO>) session.getAttribute("passengers");
        Double rate = (Double) session.getAttribute("rate"); // ensure this is Double or BigDecimal

        int passengerCount = (passengers != null) ? passengers.size() : 0;
        double totalFare = rate * passengerCount;

        model.addAttribute("bookingDTO", bookingDTO);
        model.addAttribute("passengers", passengers);
        model.addAttribute("rate", rate);
        model.addAttribute("totalFare", totalFare); // 👈 Add this

        return "user/gateway";
    }


    
    @PostMapping("/booking/confirm")
    public String confirmBookingAfterPayment(HttpSession session) {

        BookingDTO bookingDTO = (BookingDTO) session.getAttribute("bookingDTO");
        List<PassengerDTO> passengers = (List<PassengerDTO>) session.getAttribute("passengers");
        Double rate = (Double) session.getAttribute("rate");
        Integer noOfPassengers = (Integer) session.getAttribute("noOfPassengers");
        
        System.out.println("BookingDTO: " + bookingDTO);
        System.out.println("Passengers: " + passengers);
        System.out.println("Rate: " + rate);
        System.out.println("NoOfPassengers: " + noOfPassengers);

        System.out.println("UserId: " + bookingDTO.getUserId());
        System.out.println("AirlineId: " + bookingDTO.getAirlineId());
        System.out.println("FlightId: " + bookingDTO.getFlightId());
        System.out.println("ScheduleId: " + bookingDTO.getScheduleId());
        System.out.println("FlightClassId: " + bookingDTO.getFlightClassId());

        
        if (bookingDTO == null || bookingDTO.getUserId() == null || bookingDTO.getFlightId() == null ||
                bookingDTO.getAirlineId() == null || bookingDTO.getScheduleId() == null ||
                bookingDTO.getFlightClassId() == null) {
                throw new IllegalArgumentException("Missing booking details. Please re-initiate the booking process.");
            }

        
        // 1. Save Booking
        Booking booking = new Booking();
        booking.setUser(userService.getById(bookingDTO.getUserId()));
        booking.setAirline(airlineService.getById(bookingDTO.getAirlineId()));
        booking.setFlight(airFlightService.findById(bookingDTO.getFlightId()));
        booking.setSchedule(flightScheduleService.findById(bookingDTO.getScheduleId()));
        booking.setFlightClass(classService.findById(bookingDTO.getFlightClassId()));
        booking.setBookingAt(LocalDateTime.now());
        booking.setStatus("ACTIVE");
        booking = bookingRepository.save(booking); // get saved instance with ID

        // 2. Save Passengers
        for (PassengerDTO dto : passengers) {
            Passenger p = new Passenger();
            p.setBooking(booking);
            p.setUser(userService.getById(dto.getUserId()));
            p.setFlightClass(booking.getFlightClass());
            p.setSchedule(booking.getSchedule());
            p.setPname(dto.getPname());
            p.setMobileNo(dto.getMobileNo());
            p.setDob(dto.getDob());
            p.setGender(dto.getGender());
            p.setBookingAt(LocalDateTime.now());
            p.setStatus("ACTIVE");
            p.setPrn("PRN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            passengerRepository.save(p);
        }

        // 3. Save Payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setNoSeat(noOfPassengers);
        payment.setFlight(booking.getFlight());
        payment.setSchedule(booking.getSchedule());
        payment.setUser(booking.getUser());
        paymentRepository.save(payment);

        // 4. Clear session
        session.removeAttribute("bookingDTO");
        session.removeAttribute("passengers");
        session.removeAttribute("rate");
        session.removeAttribute("noOfPassengers");

        return "redirect:/booking/success";
    }


}
