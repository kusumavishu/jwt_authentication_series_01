package com.vishwesh.jwt_authentication_series_01.service.impl;

import com.vishwesh.jwt_authentication_series_01.dto.request.LoginRequest;
import com.vishwesh.jwt_authentication_series_01.dto.response.LoginResponse;
import com.vishwesh.jwt_authentication_series_01.dto.response.UserResponse;
import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;
import com.vishwesh.jwt_authentication_series_01.exception.ApiException;
import com.vishwesh.jwt_authentication_series_01.mapper.UserMapper;
import com.vishwesh.jwt_authentication_series_01.repository.UserRepository;
import com.vishwesh.jwt_authentication_series_01.security.CustomUserDetails;
import com.vishwesh.jwt_authentication_series_01.security.JwtService;
import com.vishwesh.jwt_authentication_series_01.service.AuthenticationService;
import com.vishwesh.jwt_authentication_series_01.service.RefreshTokenService;
import com.vishwesh.jwt_authentication_series_01.util.LogHelper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    public AuthenticationServiceImpl(
            UserMapper userMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,

            RefreshTokenService refreshTokenService
    ){
        this.userMapper= userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

        this.refreshTokenService = refreshTokenService;
    }


    @Override
    public UserResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return userMapper.toResponse(user);
    }


    @Override
    public LoginResponse testlogin(LoginRequest request) {
        System.out.println(">>> [LOGIN ATTEMPT] Authenticating user: " + request.getEmail());

        try {
            // 1. Hand over raw credentials to AuthenticationManager
            // This triggers CustomUserDetailsService behind the scenes!
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            System.out.println(">>> [LOGIN SUCCESS] AuthenticationManager approved user!" + LogHelper.toPrettyJson(authentication));

            // 2. Retrieve authenticated CustomUserDetails principal
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            System.out.println(">> [customUserDetails] fetched" + LogHelper.toPrettyJson(customUserDetails));

            String jwtToken = jwtService.generateAccessToken(customUserDetails);
//            String refreshToken = jwtService.generateRefreshToken();
            System.out.println(">>> [JWT GENERATED] Token generated successfully!" + jwtToken);

            UserEntity userEntity = customUserDetails.getUserEntity();

            String refreshToken = refreshTokenService.createRefreshToken(userEntity);
            System.out.println(">>> [Refresh Token Saved] !" + refreshToken);


            // 3. Return mapped user response (Later you will return a JWT Token here)
//            return userMapper.toResponse(userEntity);

            // 5. Build and return LoginResponse
            UserResponse userResponse = userMapper.toResponse(userEntity);

            System.out.println("==================================================\n");

            return LoginResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .user(userResponse)
                    .build();

        } catch (BadCredentialsException e) {
            System.err.println(">>> [LOGIN FAILED] Invalid credentials for email: " + request.getEmail());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }


    @Override
    public LoginResponse refreshAccessToken(String refreshToken){
      try{
          UserEntity user = refreshTokenService.validateRefreshToken(refreshToken)
                  .orElseThrow(() -> new RuntimeException("User not found"));

          CustomUserDetails customUserDetails = new CustomUserDetails(user);

          System.out.println(">> [IN REACCESSED]" + LogHelper.toPrettyJson(customUserDetails));

          String newAccessToken = jwtService.generateAccessToken(customUserDetails);

          System.out.println(">> [IN REACCESSED]" + newAccessToken);

          UserResponse userResponse = userMapper.toResponse(user);

          return LoginResponse
                  .builder()
                  .token(newAccessToken)
                  .refreshToken(refreshToken)
                  .user(userResponse)
                  .build();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    };

    @Override
    public UserResponse getUserWithRefresh(String refreshToken){
        try{
            UserEntity user = refreshTokenService.validateRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return userMapper.toResponse(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public UserResponse getMe(CustomUserDetails userDetails) {
        System.out.println(">>> [AUTH SERVICE] Fetching details for authenticated user: " + userDetails.getUsername());
        return userMapper.toResponse(userDetails.getUserEntity());
    }

}
