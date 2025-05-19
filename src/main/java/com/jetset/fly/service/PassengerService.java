package com.jetset.fly.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
