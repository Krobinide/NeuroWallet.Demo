package com.neurowallet.service;

import com.neurowallet.config.JwtUtil;
import com.neurowallet.dto.LoginRequest;
import com.neurowallet.dto.RegisterRequest;
import com.neurowallet.model.Role;
import com.neurowallet.model.User;
import com.neurowallet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public Map<String, Object> register(RegisterRequest request) {
        logger.info("Registering new user: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        
        User savedUser = userRepository.save(user);
        
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().name());
        
        logger.info("User registered successfully: {}", savedUser.getEmail());
        return response;
    }
    
    public Map<String, Object> login(LoginRequest request) {
        logger.info("User login attempt: {}", request.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("userId", user.getId());
        
        logger.info("User logged in successfully: {}", user.getEmail());
        return response;
    }
}