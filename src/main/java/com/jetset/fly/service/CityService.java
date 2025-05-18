package com.jetset.fly.service;

import com.jetset.fly.model.City;
import com.jetset.fly.repository.CityRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public void addCity(String cityname) {
        City city = new City();
        city.setCityname(cityname);
        cityRepository.save(city);
    }

    public List<City> getAllActiveCities() {
        return cityRepository.findByStatus("ACTIVE");
    }

    public City getById(Long id) {
        return cityRepository.findById(id).orElse(null);
    }

    public void updateCity(Long id, String newName) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            city.setCityname(newName);
            cityRepository.save(city);
        }
    }

    public void softDeleteCity(Long id) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            city.setStatus("DELETED");
            cityRepository.save(city);
        }
    }
    
    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + id));
    }
}
