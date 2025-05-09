package com.jetset.fly.service;

import com.jetset.fly.model.FlightScheduleRate;
import com.jetset.fly.repository.FlightScheduleRateRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightScheduleRateService {

    @Autowired
    private FlightScheduleRateRepository rateRepository;

    public void save(FlightScheduleRate rate) {
        rateRepository.save(rate);
    }
    
    public List<FlightScheduleRate> getBySchedule(Long scheduleId) {
        return rateRepository.findByScheduleId(scheduleId);
    }
}
