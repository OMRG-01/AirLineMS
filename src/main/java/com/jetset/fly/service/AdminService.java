package com.jetset.fly.service;

import com.jetset.fly.model.User;
import com.jetset.fly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean authenticateAdmin(String email, String password, HttpSession session) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getRole().getId() == 1) // Ensure role_id == 1
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    session.setAttribute("admin", user); // store in session
                    return true;
                })
                .orElse(false);
    }
}
