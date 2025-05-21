package com.jetset.fly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.User;
import com.jetset.fly.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmail(email.toLowerCase())
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }


    public User login(String email, String rawPassword) {
        return userRepository.findByEmail(email.toLowerCase())
                .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()))
                .orElse(null);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase()).isPresent();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }


}