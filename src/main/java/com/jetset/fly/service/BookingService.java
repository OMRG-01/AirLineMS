package com.jetset.fly.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jetset.fly.dto.ConnectedBookingDTO;
import com.jetset.fly.dto.PassengerDTO;
import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.repository.*;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AirFlightRepository flightRepo;

    @Autowired
    private FlightScheduleRepository scheduleRepo;

    @Autowired
    private ClassRepository classRepo;

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
    }
    
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    }
    
    @Transactional
    public void confirmBooking(List<ConnectedBookingDTO> connectedBookings, List<PassengerDTO> passengers) {
        List<Booking> savedBookings = new ArrayList<>();

        // Save bookings
        for (ConnectedBookingDTO dto : connectedBookings) {

            // Validate IDs explicitly before repo calls
            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("User ID is null in ConnectedBookingDTO: " + dto);
            }
            if (dto.getScheduleFlightId() == null) {
                throw new IllegalArgumentException("ScheduleFlight ID is null in ConnectedBookingDTO: " + dto);
            }
            if (dto.getFlightClassId() == null) {
                throw new IllegalArgumentException("FlightClass ID is null in ConnectedBookingDTO: " + dto);
            }

            Booking booking = new Booking();

            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

            FlightSchedule schedule = scheduleRepo.findById(dto.getScheduleFlightId())
                    .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + dto.getScheduleFlightId()));

            AirFlight flight = schedule.getFlight();
            if (flight == null) {
                throw new RuntimeException("Flight not found from schedule with ID: " + schedule.getId());
            }

            Class flightClass = classRepo.findById(dto.getFlightClassId())
                    .orElseThrow(() -> new RuntimeException("Class not found with ID: " + dto.getFlightClassId()));

            booking.setUser(user);
            booking.setFlight(flight);
            booking.setSchedule(schedule);
            booking.setFlightClass(flightClass);
            booking.setAirline(flight.getAirline());
            booking.setBookingAt(LocalDateTime.now());
            booking.setStatus("ACTIVE");

            bookingRepo.save(booking);
            savedBookings.add(booking);
        }


        // Save passengers per booking
        for (Booking booking : savedBookings) {
            for (PassengerDTO pDto : passengers) {

                if (pDto.getUserId() == null) {
                    throw new IllegalArgumentException("User ID is null in PassengerDTO: " + pDto);
                }

                Passenger passenger = new Passenger();

                passenger.setBooking(booking);
                passenger.setUser(userRepo.findById(pDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + pDto.getUserId())));
                passenger.setSchedule(booking.getSchedule());
                passenger.setFlightClass(booking.getFlightClass());

                passenger.setPname(pDto.getPname());
                passenger.setMobileNo(pDto.getMobileNo());
                passenger.setDob(pDto.getDob());
                passenger.setGender(pDto.getGender());

                passenger.setPrn("PRN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
                passenger.setBookingAt(LocalDateTime.now());
                passenger.setStatus("ACTIVE");

                passengerRepo.save(passenger);
            }
        }

     // Save payments per passenger instead of per booking
        for (Booking booking : savedBookings) {
            for (PassengerDTO pDto : passengers) {
                Payment payment = new Payment();

                payment.setBooking(booking);
                payment.setFlight(booking.getFlight());
                payment.setSchedule(booking.getSchedule());
                payment.setUser(userRepo.findById(pDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + pDto.getUserId())));

                // For each passenger, only one seat
                payment.setNoSeat(1);

                // Get rate for this booking class
                double rate = connectedBookings.stream()
                        .filter(b -> b.getFlightClassId().equals(booking.getFlightClass().getId()))
                        .map(ConnectedBookingDTO::getRate)
                        .findFirst().orElse(BigDecimal.ZERO).doubleValue();

                payment.setTotalAmount(rate);
                payment.setTransactionId(UUID.randomUUID().toString());
                payment.setCreatedAt(new Date());

                paymentRepo.save(payment);
            }
        }

    }


}
