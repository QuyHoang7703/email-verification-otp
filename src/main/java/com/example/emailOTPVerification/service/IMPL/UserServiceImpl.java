package com.example.emailOTPVerification.service.IMPL;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;



import org.springframework.stereotype.Service;

import com.example.emailOTPVerification.domain.User;
import com.example.emailOTPVerification.repository.UserRepository;
import com.example.emailOTPVerification.repository.requests.RegisterRequest;
import com.example.emailOTPVerification.repository.responses.RegisterResponse;
import com.example.emailOTPVerification.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    private final EmailService emailService;
    private final RedisService redisService;
    // private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        User existingUser = this.userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser!=null && existingUser.isVerified()){
            throw new RuntimeException("User already registered");
        }
        User user = User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(registerRequest.getPassword())
                    // .otpExpirationTime(Instant.now().plus(2, ChronoUnit.MINUTES))
                    .build();
        String otp = this.generateOTP();
        // Lưu OTP vào Redis với thời gian hết hạn là 2 phút
        redisService.saveOtp(user.getEmail(), otp, 2);
        
        // user.setOtp(otp);
        User savedUser = this.userRepository.save(user);

        sendVerificationEmail(savedUser.getEmail(), otp);
        RegisterResponse registerResponse = RegisterResponse.builder()
                                                .username(user.getUsername())
                                                .email(user.getEmail())
                                                .build();
        return registerResponse;
    }

    private String generateOTP() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    public void sendVerificationEmail(String email, String otp) {
        String subject = "Email verification";
        String body = "Your verification otp is: " + otp;
        emailService.sendEmail(email, subject, body);
    }
    // @Override
    // public void verify(String email, String otp) {
    //     User user = userRepository.findByEmail(email);
    //     if(user == null ){
    //         throw new RuntimeException("User not found");
    //     }
    //     else if(user.isVerified()){
    //         throw new RuntimeException("User is already verified");
    //     } else if(otp.equals(user.getOtp())){
    //         boolean isOtpExpired = Instant.now().isAfter(user.getOtpExpirationTime());
    //         if(isOtpExpired){
    //             throw new RuntimeException("OTP is expired");
    //         }
    //         user.setVerified(true);
    //         this.userRepository.save(user);
           

    //     }else {
    //         throw new RuntimeException("Error");
    //     }

    // }
    @Override
    public void verify(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        } else if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        // Lấy OTP từ Redis
        String storedOtp = redisService.getOtp(email);
        if (storedOtp == null) {
            throw new RuntimeException("OTP expired or not found");
        } else if (storedOtp.equals(otp)) {
            user.setVerified(true);
            this.userRepository.save(user);

            // Xóa OTP khỏi Redis sau khi xác thực thành công
            redisService.deleteOtp(email);
        } else {
            throw new RuntimeException("Invalid OTP");
        }
    }


    
}
