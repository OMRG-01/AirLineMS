package com.jetset.fly.controller;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.Airline;
import com.jetset.fly.model.City;
import com.jetset.fly.model.Class;
import com.jetset.fly.model.FlightClass;
import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.repository.AirFlightRepository;
import com.jetset.fly.repository.AirlineRepository;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;
import com.jetset.fly.service.FlightService;
import com.jetset.fly.utility.IdUtil;

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
    @Autowired
    private AirFlightRepository airFlightRepository;
    @Autowired
    private AirFlightService airFlightService;

    @GetMapping("/add-airline")
    public String showAddAirlineForm() {
        return "admin/addAirline";
    }

    @PostMapping("/add-airline")
    public String addAirline(@RequestParam("aname") String aname, Model model) {
        boolean added = airlineService.addAirline(aname);
        if (!added) {
            model.addAttribute("error", "Airline with the same name already exists!");
        } else {
            model.addAttribute("success", "Airline added successfully!");
        }
        return "admin/addAirline";
    }

    
    @GetMapping("/airline-management")
    public String viewAirline(Model model) {
        List<Airline> airlines = airlineService.getActiveAirlines();
        model.addAttribute("airlines", airlines);

        // 👉 Encode airline IDs
        Map<Long, String> encodedIdMap = new HashMap<>();
        for (Airline airline : airlines) {
            encodedIdMap.put(airline.getId(), IdUtil.encodeId(airline.getId()));
        }
        model.addAttribute("encodedIdMap", encodedIdMap);

        return "admin/viewAirline";
    }

    
    @PostMapping("/delete-airline/{id}")
    public String deleteAirline(@PathVariable Long id) {
        airlineService.softDeleteAirline(id);
        return "redirect:/admin/airline-management";
    }
    
    @GetMapping("/flights/add")
    public String showAddFlightForm(@RequestParam(value = "id", required = false) String encodedAirlineId,
                                    Model model) {
        Long selectedAirlineId = null;
        if (encodedAirlineId != null && !encodedAirlineId.isBlank()) {
            selectedAirlineId = IdUtil.decodeId(encodedAirlineId);
        }

        List<Airline> airlines = airlineService.getActiveAirlines();
        model.addAttribute("airlines", airlines);
        model.addAttribute("classes", classRepository.findByStatus("ACTIVE"));

        // 👇 This will pre-select the airline in the form dropdown
        model.addAttribute("selectedAirlineId", selectedAirlineId);

        return "admin/addFlight";
    }


    
    
    @PostMapping("/flight/add")
    public String addFlight(
            @RequestParam Long airlineId,
            @RequestParam String fnumber,
            @RequestParam int totalSeat,
            @RequestParam(name = "classIds", required = false) List<Long> classIds,
            @RequestParam Map<String, String> allParams,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            flightService.addFlightWithClasses(airlineId, fnumber, totalSeat, classIds, allParams);
            redirectAttributes.addFlashAttribute("success", "Flight added successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/flights/add";
    }


    @GetMapping("/flights/view")
    public String viewFlights(Model model) {
        List<AirFlight> flights = flightService.getAllActiveFlights();
        List<Class> allClasses = flightService.getAllClasses();

        // Build seat map: flightId -> (classId -> seat)
        Map<Long, Map<Long, Integer>> seatMap = new HashMap<>();
        for (AirFlight flight : flights) {
            Map<Long, Integer> classSeatMap = new HashMap<>();
            for (FlightClass fc : flight.getFlightClasses()) {
                classSeatMap.put(fc.getFlightClass().getId(), fc.getSeat());
            }
            seatMap.put(flight.getId(), classSeatMap);
        }
        
        Map<Long, String> encodedIdMap = new HashMap<>();
        for (AirFlight flight : flights) {
            String encodedId = Base64.getEncoder().encodeToString(flight.getId().toString().getBytes());
            encodedIdMap.put(flight.getId(), encodedId);
        }

        model.addAttribute("flights", flights);
        model.addAttribute("encodedIdMap", encodedIdMap);
        model.addAttribute("allClasses", allClasses);
        model.addAttribute("seatMap", seatMap);  // 👈 add this
        return "admin/viewFlight";
    }
    
    @GetMapping("/flights/delete/{id}")
    public String deleteFlight(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = flightService.softDeleteFlight(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Flight removed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Flight not found or already deleted.");
        }
        return "redirect:/admin/flights/view";
    }
    
    @Autowired 
    private ClassService classService;
    @Autowired 
    private FlightClassService flightClassService;
    @Autowired
    private CityService cityService;
    
    
    @GetMapping("/flights/edit/{encodedId}")
    public String showEditFlightForm(@PathVariable String encodedId, Model model) {
        try {
            Long id = Long.parseLong(new String(Base64.getDecoder().decode(encodedId)));
            AirFlight flight = airFlightService.findById(id);
            List<Class> classes = classService.getAllClasses();
            List<Airline> airlines = airlineService.getAllAirlines();
            List<FlightClass> flightClasses = flightClassService.getByFlightId(id);

            Map<Long, Integer> seatMap = new HashMap<>();
            for (FlightClass fc : flightClasses) {
                seatMap.put(fc.getFlightClass().getId(), fc.getSeat());
            }

            model.addAttribute("flight", flight);
            model.addAttribute("classes", classes);
            model.addAttribute("airlines", airlines);
            model.addAttribute("selectedAirlineId", flight.getAirline().getId());
            model.addAttribute("seatMap", seatMap);

            // Send back encoded ID for form submission
            model.addAttribute("encodedId", encodedId);

            return "admin/editFlight";
        } catch (Exception e) {
            return "redirect:/admin/flights/view";
        }
    }

    
    

    @PostMapping("/flights/update")
    public String updateFlight(@RequestParam Map<String, String> params,
                               @RequestParam(name = "classIds", required = false) List<Long> classIds,
                               RedirectAttributes redirectAttributes) {
        try {
            // Decode the ID
            String encodedId = params.get("encodedId");
            Long flightId = Long.parseLong(new String(Base64.getDecoder().decode(encodedId)));

            Long airlineId = Long.parseLong(params.get("airlineId"));
            String fnumber = params.get("fnumber");
            int totalSeat = Integer.parseInt(params.get("totalSeat"));

            AirFlight flight = airFlightService.findById(flightId);
            Airline airline = airlineService.findById(airlineId);
            flight.setAirline(airline);
            flight.setFnumber(fnumber);
            flight.setTotalSeat(totalSeat);
            airFlightService.save(flight);

            List<FlightClass> existingFlightClasses = flightClassService.getByFlightId(flightId);
            Map<Long, FlightClass> existingMap = new HashMap<>();
            for (FlightClass fc : existingFlightClasses) {
                existingMap.put(fc.getFlightClass().getId(), fc);
            }

            if (classIds != null) {
                for (Long classId : classIds) {
                    int seat = Integer.parseInt(params.get("seat_" + classId));
                    if (existingMap.containsKey(classId)) {
                        FlightClass fc = existingMap.get(classId);
                        fc.setSeat(seat);
                        flightClassService.save(fc);
                    } else {
                        FlightClass newFc = new FlightClass();
                        newFc.setFlight(flight);
                        newFc.setFlightClass(classService.findById(classId));
                        newFc.setSeat(seat);
                        flightClassService.save(newFc);
                    }
                }
            }

            return "redirect:/admin/flights/view";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed: " + e.getMessage());
            return "redirect:/admin/flights/edit/" + params.get("encodedId");
        }
    }


    
    
    




}
