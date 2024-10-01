package com.example.emailOTPVerification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.emailOTPVerification.repository.requests.RegisterRequest;
import com.example.emailOTPVerification.repository.responses.RegisterResponse;
import com.example.emailOTPVerification.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor // create constructor with attributes with final
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register (@RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(registerResponse);
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String otp) {
        try{
            userService.verify(email, otp);
            return ResponseEntity.ok("User verified successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
