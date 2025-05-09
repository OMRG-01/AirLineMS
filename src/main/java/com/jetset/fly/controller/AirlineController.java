package com.jetset.fly.controller;

import java.time.LocalDateTime;
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
        model.addAttribute("classes", classRepository.findByStatus("ACTIVE"));
        return "admin/addFlight";
    }
    
    
    @PostMapping("/flight/add")
    public String addFlight(
    		@RequestParam Long airlineId,
                            @RequestParam String fnumber,
                            @RequestParam int totalSeat,
                            @RequestParam(name = "classIds") List<Long> classIds,
                            @RequestParam Map<String, String> allParams) {

        // Send everything to the service layer
        flightService.addFlightWithClasses(airlineId, fnumber, totalSeat, classIds, allParams);
        
        
        return "redirect:/admin/flights/add?success"; // Redirect with success
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

        model.addAttribute("flights", flights);
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
    
    
    @GetMapping("/flights/edit/{id}")
    public String showEditFlightForm(@PathVariable Long id, Model model) {
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

        return "admin/editFlight";
    }
    
    

    @PostMapping("/flights/update")
    public String updateFlight(@RequestParam Map<String, String> params,
                               @RequestParam(name = "classIds", required = false) List<Long> classIds) {
        Long flightId = Long.parseLong(params.get("id"));
        Long airlineId = Long.parseLong(params.get("airlineId"));
        String fnumber = params.get("fnumber");
        int totalSeat = Integer.parseInt(params.get("totalSeat"));

        // ✅ Find and update the AirFlight
        AirFlight flight = airFlightService.findById(flightId);
        Airline airline = airlineService.findById(airlineId);  // Use proper method, not constructor
        flight.setAirline(airline);
        flight.setFnumber(fnumber);
        flight.setTotalSeat(totalSeat);
        airFlightService.save(flight);

        // ✅ Get existing FlightClass entries
        List<FlightClass> existingFlightClasses = flightClassService.getByFlightId(flightId);

        // Create map: classId → FlightClass
        Map<Long, FlightClass> existingMap = new HashMap<>();
        for (FlightClass fc : existingFlightClasses) {
            existingMap.put(fc.getFlightClass().getId(), fc);
        }

        // ✅ Update or insert class seat info
        if (classIds != null) {
            for (Long classId : classIds) {
                int seat = Integer.parseInt(params.get("seat_" + classId));

                if (existingMap.containsKey(classId)) {
                    // UPDATE existing class seat
                    FlightClass fc = existingMap.get(classId);
                    fc.setSeat(seat);
                    flightClassService.save(fc);
                } else {
                    // INSERT new class entry
                    FlightClass newFc = new FlightClass();
                    newFc.setFlight(flight);
                    newFc.setFlightClass(classService.findById(classId)); // Use method, not constructor
                    newFc.setSeat(seat);
                    flightClassService.save(newFc);
                }
            }
        }

        return "redirect:/admin/flights/view";
    }

    
    
    




}
