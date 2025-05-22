package com.jetset.fly.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jetset.fly.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	
	
	@Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p")
	Double getTotalEarnings();
	
	@Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE DATE(p.createdAt) = CURRENT_DATE")
	Double getTodaysEarnings();
	
	@Query("SELECT COALESCE(SUM(p.noSeat), 0) FROM Payment p")
	Double getTotalBookings();

	@Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
	List<Payment> findLatestPayment(Pageable pageable);


}
