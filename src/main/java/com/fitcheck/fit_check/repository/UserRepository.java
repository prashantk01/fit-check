package com.fitcheck.fit_check.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitcheck.fit_check.model.user.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}