package com.jetset.fly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
