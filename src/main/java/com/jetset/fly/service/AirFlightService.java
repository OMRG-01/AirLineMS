package com.jetset.fly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.repository.AirFlightRepository;

@Service
public class AirFlightService {

    @Autowired
    private AirFlightRepository airFlightRepository;

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

}
