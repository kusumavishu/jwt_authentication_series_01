package com.vishwesh.jwt_authentication_series_01.service;

import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RefreshTokenService {
    String createRefreshToken(UserEntity user);

    Optional<UserEntity> validateRefreshToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

    void revokeAllUserTokens(UserEntity user);
}
