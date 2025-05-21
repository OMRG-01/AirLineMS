package com.jetset.fly.service;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.dto.ViewPassengerDTO;
import com.jetset.fly.model.Passenger;
import com.jetset.fly.repository.FlightClassRepository;
import com.jetset.fly.repository.PassengerRepository;

@Service
public class PassengerService {
	
    @Autowired
    private FlightClassRepository flightClassRepository;
    
    @Autowired
    private PassengerRepository  passengerRepository;

    public int countPassengersByScheduleAndClass(Long scheduleId, Long classId) {
        return passengerRepository.countByScheduleIdAndFlightClassId(scheduleId, classId);
    }

    public List<Passenger> findByBookingId(Long bookingId) {
        return passengerRepository.findByBookingId(bookingId);
    }
    public List<Passenger> getPassengersByBookingId(Long bookingId) {
        return passengerRepository.findByBookingId(bookingId);
    }
    
    public List<ViewPassengerDTO> getAllPassengerDetails() {
        List<Passenger> passengers = passengerRepository.findAll(); // or use filter if needed
        return passengers.stream()
                .map(ViewPassengerDTO::new)
                .collect(Collectors.toList());
    }

}
