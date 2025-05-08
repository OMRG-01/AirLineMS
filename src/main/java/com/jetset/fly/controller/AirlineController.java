package com.jetset.fly.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.jetset.fly.model.Airline;
import com.jetset.fly.repository.AirlineRepository;
import com.jetset.fly.repository.ClassRepository;
import com.jetset.fly.service.AirlineService;
import com.jetset.fly.service.FlightService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin")
public class AirlineController {

    @Autowired
    private AirlineService airlineService;
    @Autowired
    private AirlineRepository airlineRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private FlightService flightService;

    @GetMapping("/add-airline")
    public String showAddAirlineForm() {
        return "admin/addAirline";
    }

    @PostMapping("/add-airline")
    public String addAirline(@RequestParam("aname") String aname, Model model) {
        airlineService.addAirline(aname);
        model.addAttribute("success", "Airline added successfully!");
        return "admin/addAirline";
    }
    
    @GetMapping("/airline-management")
    public String viewAirline(Model model) {
    	 model.addAttribute("airlines", airlineService.getActiveAirlines());
        return "admin/viewAirline";
    }
    
    @PostMapping("/delete-airline/{id}")
    public String deleteAirline(@PathVariable Long id) {
        airlineService.softDeleteAirline(id);
        return "redirect:/admin/airline-management";
    }
    
    @GetMapping("/flights/add")
    public String showAddFlightForm(@RequestParam(value = "id", required = false) Long selectedAirlineId,
                                    Model model) {
    	List<Airline> airlines = airlineService.getActiveAirlines();
        model.addAttribute("airlines", airlineRepository.findByStatus("ACTIVE"));
        model.addAttribute("classes", classRepository.findAll());
        return "admin/addFlight";
    }
    
    
    @PostMapping("/flight/add")
    public String addFlight(@RequestParam Long airlineId,
                            @RequestParam String fnumber,
                            @RequestParam int totalSeat,
                            @RequestParam(name = "classIds") List<Long> classIds,
                            @RequestParam Map<String, String> allParams) {

        // Send everything to the service layer
        flightService.addFlightWithClasses(airlineId, fnumber, totalSeat, classIds, allParams);

        return "redirect:/admin/flights/add?success"; // Redirect with success
    }


}
