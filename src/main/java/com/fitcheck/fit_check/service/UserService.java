package com.fitcheck.fit_check.service;

import java.util.Optional;

import com.fitcheck.fit_check.model.user.User;
import com.fitcheck.fit_check.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Method to create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Method to find a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}