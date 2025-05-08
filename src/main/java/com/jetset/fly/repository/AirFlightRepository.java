package com.jetset.fly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.AirFlight;

public interface AirFlightRepository extends JpaRepository<AirFlight, Long> {
    boolean existsByFnumber(String fnumber); // Optional: prevent duplicate flight numbers
}
