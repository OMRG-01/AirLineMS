package com.jetset.fly.repository;


import com.jetset.fly.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByStatus(String status);
    Optional<City> findByCityname(String cityname);
}
