package com.jetset.fly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.User;
import com.jetset.fly.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User login(String email, String rawPassword) {
        return userRepository.findByEmail(email.toLowerCase())
                .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()))
                .orElse(null);
    }
}