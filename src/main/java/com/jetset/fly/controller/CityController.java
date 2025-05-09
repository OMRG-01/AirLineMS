package com.jetset.fly.controller;

import com.jetset.fly.model.City;
import com.jetset.fly.model.Class;
import com.jetset.fly.service.CityService;
import com.jetset.fly.service.ClassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/cities")
public class CityController {

    @Autowired
    private CityService cityService;
    
    @Autowired
    private ClassService classService;

    // 1. Go to addCity.html
    @GetMapping("/add")
    public String showAddCityForm() {
        return "admin/addCity";
    }

    // 2. Submit POST to add city
    @PostMapping("/add")
    public String addCity(@RequestParam("cityname") String cityname) {
        cityService.addCity(cityname);
        return "redirect:/admin/cities/view";
    }
    
    @GetMapping("/class/delete/{id}")
    public String deleteClass(@PathVariable("id") Long id) {
        classService.softDeleteClass(id);
        return "redirect:/admin/cities/view";
    }
    
    @PostMapping("/class/add")
    public String addClass(@RequestParam("classname") String classname) {
        classService.addClass(classname);
        return "redirect:/admin/cities/view";
    }
    


    // 3. View all active cities
    @GetMapping("/view")
    public String viewCities(Model model) {
        List<City> cities = cityService.getAllActiveCities();
        List<Class> classes = classService.getAllActiveClasses();
        model.addAttribute("cities", cities);
        model.addAttribute("classes",classes);
        return "admin/viewCity";
    }

    // 4. Go to update form
    @GetMapping("/edit/{id}")
    public String showEditCityForm(@PathVariable Long id, Model model) {
        City city = cityService.getById(id);
        model.addAttribute("city", city);
        return "admin/updateCity";
    }

    // 5. Submit update
    @PostMapping("/update")
    public String updateCity(@RequestParam("id") Long id,
                             @RequestParam("cityname") String cityname) {
        cityService.updateCity(id, cityname);
        return "redirect:/admin/cities/view";
    }

    // 6. Soft delete city
    @GetMapping("/delete/{id}")
    public String deleteCity(@PathVariable("id") Long id) {
        cityService.softDeleteCity(id);
        return "redirect:/admin/cities/view";
    }
}
