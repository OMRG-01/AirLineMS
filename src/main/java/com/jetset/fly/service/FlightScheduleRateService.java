package com.jetset.fly.service;

import com.jetset.fly.model.FlightClass;
import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.model.FlightScheduleRate;
import com.jetset.fly.repository.FlightClassRepository;
import com.jetset.fly.repository.FlightScheduleRateRepository;
import com.jetset.fly.repository.FlightScheduleRepository;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightScheduleRateService {

    @Autowired
    private FlightScheduleRateRepository rateRepository;
    
    @Autowired
    private FlightScheduleRepository scheduleRepository;

    @Autowired
    private FlightClassRepository classRepository;


    public void save(FlightScheduleRate rate) {
        rateRepository.save(rate);
    }
    
    public List<FlightScheduleRate> getBySchedule(Long scheduleId) {
        return rateRepository.findByScheduleId(scheduleId);
    }
    
    public void updateOrCreateRate(Long scheduleId, Long classId, Double cost) {
        FlightSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        FlightClass flightClass = classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Flight class not found"));

        // Try to find existing rate
        Optional<FlightScheduleRate> optionalRate = rateRepository.findByScheduleAndFlightClass(schedule, flightClass);

        FlightScheduleRate rate;
        if (optionalRate.isPresent()) {
            rate = optionalRate.get();
            rate.setRate(cost);
        } else {
        	 rate = new FlightScheduleRate();
             rate.setSchedule(schedule);
             rate.setFlight(schedule.getFlight());  // Important: set flight here!
             rate.setRate(cost);
        }

        rateRepository.save(rate);
    }
}
