package com.onlineshop.user_service.controllers;

import com.onlineshop.user_service.dtos.AuthRequest;
import com.onlineshop.user_service.dtos.AuthResponse;
import com.onlineshop.user_service.dtos.UserRequest;
import com.onlineshop.user_service.entities.User;
import com.onlineshop.user_service.repositories.UserRepository;
import com.onlineshop.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request) {
        if(repo.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("User already exists");

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        repo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = repo.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword()))
            return ResponseEntity.badRequest().body("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));

    }

}
