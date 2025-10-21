package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserApiService userApiService;

    public UserApiController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if(userApiService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        userApiService.register(user);
        return ResponseEntity.ok("Registration successful!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userApiService.findByUsername(loginRequest.getUsername());
        if(userOpt.isEmpty() || !userApiService.checkPassword(userOpt.get(), loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        String token = JwtUtil.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok(token);
    }
}
