package io.github.yusenshen.recipe.controller;

import io.github.yusenshen.recipe.dto.LoginRequest;
import io.github.yusenshen.recipe.dto.RegisterRequest;
import io.github.yusenshen.recipe.model.User;
import io.github.yusenshen.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // user database access
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists"));
        }

        // save new user
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // verify username and password
        User found = userRepository.findByUsername(request.getUsername());
        if (found == null || !found.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }

        return ResponseEntity.ok(Map.of("token", request.getUsername()));
    }

}
