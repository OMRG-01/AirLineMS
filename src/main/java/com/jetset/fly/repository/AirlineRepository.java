package com.jetset.fly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.Airline;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
	List<Airline> findByStatus(String status);

}
