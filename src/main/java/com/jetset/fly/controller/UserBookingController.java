package com.jetset.fly.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jetset.fly.dto.*;
import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserBookingController {

    @Autowired private UserService userService;
    @Autowired private AirFlightService airFlightService;
    @Autowired private AirlineService airlineService;
    @Autowired private FlightScheduleService flightScheduleService;
    @Autowired private ClassService classService;
    @Autowired private EmailService emailService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PassengerRepository passengerRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private BookingService bookingService;
    // 🟢 STEP 1: Start booking - Store request initial details
    @PostMapping("/book-connecting")
    public String initiateConnectedFlightBooking(
            @RequestParam Long flightClassId,
            @RequestParam BigDecimal rate,
            @RequestParam Long userId,
            @RequestParam Integer noOfPassengers,
            @RequestParam List<Long> scheduleFlightIds,
            HttpSession session,
            Model model) {

        List<ConnectedBookingDTO> bookingList = new ArrayList<>();

        for (Long scheduleId : scheduleFlightIds) {
            ConnectedBookingDTO dto = new ConnectedBookingDTO();
            dto.setScheduleFlightId(scheduleId);
            dto.setFlightClassId(flightClassId);
            dto.setRate(rate);
            dto.setUserId(userId);
            dto.setNoOfPassengers(noOfPassengers);
            bookingList.add(dto);
        }

        // Save DTO list in session for next steps
        session.setAttribute("connectedBookings", bookingList);

        // Calculate total fare = rate * number of passengers
        BigDecimal totalFare = rate.multiply(BigDecimal.valueOf(noOfPassengers));

        // Add rate and totalFare to model to show on passenger details page
        model.addAttribute("rate", rate);
        model.addAttribute("totalFare", totalFare);
        model.addAttribute("nos_passanger", noOfPassengers);
	    model.addAttribute("userId", userId);

        return "user/passangerDetailForm";
    }



    @GetMapping("/gateway2")
    public String showPaymentPage(HttpSession session, Model model) {
        List<ConnectedBookingDTO> bookings = (List<ConnectedBookingDTO>) session.getAttribute("connectedBookings");
        List<PassengerDTO> passengers = (List<PassengerDTO>) session.getAttribute("tempPassengers");

        if (bookings == null || passengers == null) {
            // Handle error or redirect somewhere safe
            return "redirect:/user/passangerDetailForm";
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("passengers", passengers);

        return "user/gateway2";
    }


    @PostMapping("/confirm-payment")
    public String confirmPayment(HttpSession session) {
        List<ConnectedBookingDTO> connectedBookings = (List<ConnectedBookingDTO>) session.getAttribute("connectedBookings");
        List<PassengerDTO> tempPassengers = (List<PassengerDTO>) session.getAttribute("tempPassengers");

        if (connectedBookings == null || tempPassengers == null) {
            return "redirect:/user/passangerDetailForm";
        }

        bookingService.confirmBooking(connectedBookings, tempPassengers);

        // Clear session attributes after successful booking
        session.removeAttribute("connectedBookings");
        session.removeAttribute("tempPassengers");

        return "user/paymentSuccess";
    }
}
