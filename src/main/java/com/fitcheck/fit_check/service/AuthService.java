package com.fitcheck.fit_check.service;

import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fitcheck.fit_check.model.user.User;
import com.fitcheck.fit_check.repository.UserRepository;
import com.fitcheck.fit_check.dto.auth.AuthLogin;
import com.fitcheck.fit_check.dto.auth.AuthRegister;
import com.fitcheck.fit_check.dto.auth.AuthResponse;
import com.fitcheck.fit_check.enums.Roles;
import com.fitcheck.fit_check.exception.BadCredentialException;
import com.fitcheck.fit_check.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    };

    public AuthResponse login(AuthLogin authLogin) {
        Optional<User> user = userRepository.findByUsername(authLogin.username());
        if (user.isEmpty()) {
            throw new BadCredentialException("Invalid username or password");
        }
        if (bCryptPasswordEncoder.matches(authLogin.password(), user.get().getPassword()) == false) {
            throw new BadCredentialException("Invalid username or password");
        }
        String jwtToken = jwtService.generateToken(
                user.get().getUsername(),
                user.get().getRoles().stream().toList());

        return new AuthResponse(
                user.get().getUsername(),
                user.get().getEmail(),
                user.get().getRoles(),
                jwtToken,
                "Bearer",
                3600L // Token expiry time in seconds
        );
    }

    public AuthResponse register(AuthRegister authRegister) {
        System.out.println(">>> Entered REGISTER METHOD IN AUTH SERVICE");
        Optional<User> existingUser = userRepository.findByUsername(authRegister.username());

        if (existingUser.isPresent()) {
            throw new BadCredentialException("Username is already taken");
        }
        Optional<User> existingEmail = userRepository.findByEmail(authRegister.email());
        if (existingEmail.isPresent()) {
            throw new BadCredentialException("Email is already registered");
        }
        if (authRegister.username().length() < 5 || authRegister.username().length() > 20) {
            throw new BadCredentialException("Username must be between 5 and 20 characters");
        }
        if (!authRegister.email().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new BadCredentialException("Email must be a valid email address");
        }
        if (authRegister.password().length() < 8 || authRegister.password().length() > 64) {
            throw new BadCredentialException("Password must be at least 8 characters long");
        }
        if (!authRegister.password().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[+@#$&%]).{8,}$")) {
            throw new BadCredentialException(
                    "Password must contain at least one lowercase character, one uppercase character, one number, and one special character (+@#$&%) and be at least 8 characters long");
        }

        User newUser = new User();
        Set<Roles> role = Set.of(Roles.USER);
        newUser.setUsername(authRegister.username());
        newUser.setEmail(authRegister.email());
        newUser.setRoles(role);
        newUser.setPassword(bCryptPasswordEncoder.encode(authRegister.password()));
        userRepository.save(newUser);

        String jwtToken = jwtService.generateToken(
                newUser.getUsername(),
                newUser.getRoles().stream().toList());

        AuthResponse authResponse = new AuthResponse(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getRoles(),
                jwtToken,
                "Bearer",
                3600L // Token expiry time in seconds
        );
        return authResponse;
    }

}