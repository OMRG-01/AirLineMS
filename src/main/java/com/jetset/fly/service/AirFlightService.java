package com.jetset.fly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.Airline;
import com.jetset.fly.repository.AirFlightRepository;
import com.jetset.fly.repository.AirlineRepository;

@Service
public class AirFlightService {

    @Autowired
    private AirFlightRepository airFlightRepository;
    
    @Autowired
    private AirlineRepository airlineRepository;

    public AirFlight findById(Long id) {
        return airFlightRepository.findById(id).orElse(null);
    }

    public void save(AirFlight flight) {
        airFlightRepository.save(flight);
    }
    
    public long countByStatus(String status) {
        return airFlightRepository.countByStatus(status);
    }

    public AirFlight findLatestByStatus(String status) {
        return airFlightRepository.findTopByStatusOrderByIdDesc(status);
    }
    
    public List<AirFlight> getFlightsByAirline(Long airlineId) {
        return airFlightRepository.findByAirlineIdAndStatus(airlineId, "ACTIVE");
    }

    public AirFlight saveFlight(AirFlight flight) {
        // Save the flight first
        AirFlight savedFlight = airFlightRepository.save(flight);

        // Increase flight count for the related airline
        Airline airline = savedFlight.getAirline();
        airline.setNoOfFlight(airline.getNoOfFlight() + 1);
        airlineRepository.save(airline);

        return savedFlight;
    }
    
    public int countByAirlineAndStatus(Airline airline, String status) {
        return airFlightRepository.countByAirlineAndStatus(airline, status);
    }

}
