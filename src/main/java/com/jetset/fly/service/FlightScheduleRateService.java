package com.jetset.fly.service;

import com.jetset.fly.model.FlightClass;
import com.jetset.fly.model.FlightSchedule;

import com.jetset.fly.model.Class;
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
    
    public void updateOrCreateRate(Long scheduleId, Long flightClassId, Double cost) {
        FlightSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        FlightClass flightClassEntity = classRepository.findById(flightClassId)
                .orElseThrow(() -> new EntityNotFoundException("Flight class not found"));

        Class seatClass = flightClassEntity.getFlightClass();

        Optional<FlightScheduleRate> optionalRate = rateRepository.findByScheduleAndFlightClass(schedule, seatClass);

        FlightScheduleRate rate;
        if (optionalRate.isPresent()) {
            rate = optionalRate.get();
            rate.setRate(cost);
        } else {
            rate = new FlightScheduleRate();
            rate.setSchedule(schedule);
            rate.setFlight(schedule.getFlight());
            rate.setFlightClass(seatClass);
            rate.setRate(cost);
        }

        rateRepository.save(rate);
    }
    
    public List<FlightScheduleRate> findByScheduleId(Long scheduleId) {
        return rateRepository.findByScheduleId(scheduleId);
    }

    public void deleteByScheduleId(Long scheduleId) {
    	rateRepository.deleteByScheduleId(scheduleId);
    }
    
    public List<FlightScheduleRate> getRatesByScheduleId(Long scheduleId) {
        return rateRepository.findByScheduleId(scheduleId);
    }



}
