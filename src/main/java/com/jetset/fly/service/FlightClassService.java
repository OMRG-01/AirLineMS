package com.jetset.fly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.FlightClass;
import com.jetset.fly.repository.FlightClassRepository;

@Service
public class FlightClassService {

    @Autowired
    private FlightClassRepository flightClassRepository;

    public List<FlightClass> getByFlightId(Long flightId) {
        return flightClassRepository.findByFlightId(flightId);
    }

    public void deleteByFlightId(Long flightId) {
        flightClassRepository.deleteByFlightId(flightId);
    }

    public void save(FlightClass flightClass) {
        flightClassRepository.save(flightClass);
    }
    
    public FlightClass getById(Long id) {
        return flightClassRepository.findById(id).orElse(null);
    }

    
    public List<FlightClass> findByFlightAndFlightStatus(Long flightId, String status) {
        return flightClassRepository.findByFlight_IdAndFlight_Status(flightId, status);
    }
    
    public List<FlightClass> findByFlightId(Long flightId) {
        return flightClassRepository.findByFlightId(flightId);
    }
    
    public FlightClass findByFlightIdAndClassId(Long flightId, Long classId) {
        return flightClassRepository.findByFlightIdAndClassId(flightId, classId);
    }

}
