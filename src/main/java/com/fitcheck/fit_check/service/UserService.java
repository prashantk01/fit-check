package com.fitcheck.fit_check.service;

import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitcheck.fit_check.dto.user.UserCreate;
import com.fitcheck.fit_check.dto.user.UserResponse;
import com.fitcheck.fit_check.enums.AuthProvider;
import org.springframework.dao.DuplicateKeyException;
import com.fitcheck.fit_check.exception.ResourceNotFoundException;
import com.fitcheck.fit_check.mapper.UserMapper;
import com.fitcheck.fit_check.model.user.User;
import com.fitcheck.fit_check.repository.AuthRepository;

@Service
public class UserService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(AuthRepository authRepository, BCryptPasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Method to create a new user
    public UserResponse createUser(UserCreate userDTO) {
        // check if username or email already exists
        if (authRepository.existsByEmail(userDTO.email()) || authRepository.existsByUsername(userDTO.username())) {
            throw new DuplicateKeyException("Username or Email already exists");
        }
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRoles(Set.of("ROLE_USER")); // Assign default role
        user.setAuthProvider(AuthProvider.LOCAL);
        User createdUser = authRepository.save(user);
        return UserMapper.toResponse(createdUser);
    }

    public UserResponse getUserById(String id) {
        User user = authRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toResponse(user);
    }

}