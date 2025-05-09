package com.jetset.fly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.Airline;
import com.jetset.fly.model.FlightClass;

import com.jetset.fly.model.Class;

public interface AirFlightRepository extends JpaRepository<AirFlight, Long> {
    boolean existsByFnumber(String fnumber); // Optional: prevent duplicate flight numbers
    
    List<AirFlight> findByStatus(String status);
    
    long countByStatus(String status);
    AirFlight findTopByStatusOrderByIdDesc(String status);
    
    List<AirFlight> findByAirlineIdAndStatus(Long airlineId, String status);


    int countByAirlineAndStatus(Airline airline, String status);

}
