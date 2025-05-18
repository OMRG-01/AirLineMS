package com.jetset.fly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jetset.fly.model.Class;
import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.FlightClass;

public interface FlightClassRepository extends JpaRepository<FlightClass, Long> {
    List<FlightClass> findByFlightId(Long flightId);
    
    Optional<FlightClass> findByFlightAndFlightClass(AirFlight flight, Class flightClass);

    void deleteByFlightId(Long flightId);
    
        List<FlightClass> findByFlight_IdAndFlight_Status(Long flightId, String status);
  

}
