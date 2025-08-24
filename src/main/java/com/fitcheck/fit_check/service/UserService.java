package com.fitcheck.fit_check.service;

import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitcheck.fit_check.dto.user.UserCreate;
import com.fitcheck.fit_check.dto.user.UserResponse;
import com.fitcheck.fit_check.enums.AuthProvider;
import com.fitcheck.fit_check.mapper.UserMapper;
import com.fitcheck.fit_check.model.user.User;
import com.fitcheck.fit_check.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Method to create a new user
    public UserResponse createUser(UserCreate userDTO) {
        // check if username or email already exists
        if (userRepository.existsByEmail(userDTO.email()) || userRepository.existsByUsername(userDTO.username())) {
            throw new IllegalArgumentException("Username or Email already exists");
        }
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRoles(Set.of("ROLE_USER")); // Assign default role
        user.setAuthProvider(AuthProvider.LOCAL);
        User createdUser = userRepository.save(user);
        return UserMapper.toResponse(createdUser);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserMapper.toResponse(user);
    }

}