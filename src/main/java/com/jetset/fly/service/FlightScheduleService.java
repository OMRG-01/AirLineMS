package com.jetset.fly.service;

import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.repository.FlightScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightScheduleService {

    @Autowired
    private FlightScheduleRepository scheduleRepository;

    public FlightSchedule save(FlightSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<FlightSchedule> findAll() {
        return scheduleRepository.findAll();
    }

    public FlightSchedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}
