package com.example.emailOTPVerification.repository.responses;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterResponse {
    private String username;
    private String email;
}
