package com.jetset.fly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.Airline;
import com.jetset.fly.repository.AirlineRepository;

@Service
public class AirlineService {

    @Autowired
    private AirlineRepository airlineRepo;

    public void addAirline(String name) {
        Airline airline = new Airline();
        airline.setAname(name);
        airlineRepo.save(airline);
    }
    
    public List<Airline> getActiveAirlines() {
        return airlineRepo.findByStatus("ACTIVE");
    }

    public void softDeleteAirline(Long id) {
        Airline airline = airlineRepo.findById(id).orElseThrow();
        airline.setStatus("DELETED");
        airlineRepo.save(airline);
    }
    
    public List<Airline> getAllAirlines() {
        return airlineRepo.findAll();
    }
    
    public Airline findById(Long id) {
        return airlineRepo.findById(id).orElseThrow(() -> new RuntimeException("Airline not found"));
    }


    public long countByStatus(String status) {
        return airlineRepo.countByStatus(status);
    }

    public Airline findLatestByStatus(String status) {
        return airlineRepo.findTopByStatusOrderByIdDesc(status);
    }

}
