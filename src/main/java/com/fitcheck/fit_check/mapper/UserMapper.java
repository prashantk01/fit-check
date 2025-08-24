package com.fitcheck.fit_check.mapper;

import com.fitcheck.fit_check.dto.user.UserCreate;
import com.fitcheck.fit_check.dto.user.UserResponse;
import com.fitcheck.fit_check.model.user.User;

public class UserMapper {

    public static User toEntity(UserCreate dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getAuthProvider().name());
    }
}
