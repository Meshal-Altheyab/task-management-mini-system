package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.RegisterRequest;
import com.example.demo.service.RegistrationService;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

	@Autowired
	private RegistrationService authService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

		authService.register(request);
		return ResponseEntity.ok("User registered successfully");
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request) {
		
		
		String token = authService.login(request);
		return ResponseEntity.ok(token);
	}
}
