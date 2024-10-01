package com.example.emailOTPVerification.service;

import com.example.emailOTPVerification.repository.requests.RegisterRequest;
import com.example.emailOTPVerification.repository.responses.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);

    void verify(String email, String otp);
}
