package com.vishwesh.jwt_authentication_series_01.service;

import com.vishwesh.jwt_authentication_series_01.dto.request.LoginRequest;
import com.vishwesh.jwt_authentication_series_01.dto.response.LoginResponse;
import com.vishwesh.jwt_authentication_series_01.dto.response.UserResponse;

import com.vishwesh.jwt_authentication_series_01.security.CustomUserDetails;

public interface AuthenticationService {
    UserResponse login(LoginRequest request);
    LoginResponse testlogin(LoginRequest request);

    UserResponse getMe(CustomUserDetails userDetails);
}
