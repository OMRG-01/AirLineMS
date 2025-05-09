package com.jetset.fly.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.repository.ClassRepository;
import com.jetset.fly.model.City;
import com.jetset.fly.model.Class;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }
    
    public List<Class> getAllActiveClasses() {
        return classRepository.findByStatus("ACTIVE");
    }
    
    public Class findById(Long id) {
        return classRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));
    }
    
    public void softDeleteClass(Long id) {
        Optional<Class> optionalClass = classRepository.findById(id);
        if (optionalClass.isPresent()) {
        	Class classes = optionalClass.get();
        	classes.setStatus("DELETED");
            classRepository.save(classes);
        }
    }

    public void addClass(String classname) {
        Class classes = new Class();
        classes.setName(classname);
        classRepository.save(classes);
    }
}
