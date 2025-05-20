package com.jetset.fly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
