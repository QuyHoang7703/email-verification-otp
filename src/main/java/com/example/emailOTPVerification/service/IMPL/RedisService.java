package com.example.emailOTPVerification.service.IMPL;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(String email, String otp, long expirationTimeInMinutes) {
        // Lưu OTP với TTL
        redisTemplate.opsForValue().set(email, otp, expirationTimeInMinutes, TimeUnit.MINUTES);
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteOtp(String email) {
        redisTemplate.delete(email);
    }
    
}
