package com.vishwesh.jwt_authentication_series_01.controller;

import com.vishwesh.jwt_authentication_series_01.dto.request.LoginRequest;
import com.vishwesh.jwt_authentication_series_01.dto.request.RefreshTokenRequest;
import com.vishwesh.jwt_authentication_series_01.dto.response.ApiResponse;
import com.vishwesh.jwt_authentication_series_01.dto.response.LoginResponse;
import com.vishwesh.jwt_authentication_series_01.dto.response.UserResponse;
import com.vishwesh.jwt_authentication_series_01.service.AuthenticationService;
import com.vishwesh.jwt_authentication_series_01.security.CustomUserDetails;
import com.vishwesh.jwt_authentication_series_01.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(
            AuthenticationService authenticationService
    ){
        this.authenticationService = authenticationService;
    }


    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.testlogin(request);
        return new ApiResponse<>("Login Successful", response);
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken
    ){
        LoginResponse response = authenticationService.refreshAccessToken(refreshToken);
        return new ApiResponse<>("Token refreshed successfully",response);
    };

    @PostMapping("/refresh/me")
    public ApiResponse<UserResponse> getUser(
            @RequestHeader("X-Refresh-Token") String refreshToken
    ){
        System.out.println("refreshToken" + refreshToken);
        UserResponse response = authenticationService.getUserWithRefresh(refreshToken);

        return new ApiResponse<>(
                "User found successfully",
                response
        );
    }


    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        System.out.println(">>> [USER API] Fetching details for user: " + userDetails.getUsername());
        UserResponse userResponse = authenticationService.getMe(userDetails);
        return new ApiResponse<>("Details Fetched", userResponse);
    }


}



//    @PostMapping("/login")
//    public UserResponse login(@Valid @RequestBody LoginRequest request) {
//        System.out.println(">>>>> USER <<<<<" + request.getEmail());
//
//        UserEntity user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Check password here
//        System.out.println(">>>> printing user <<<<<");
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        return userMapper.toResponse(user);
//    }