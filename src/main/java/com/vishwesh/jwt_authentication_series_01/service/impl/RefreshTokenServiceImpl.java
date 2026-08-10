package com.vishwesh.jwt_authentication_series_01.service.impl;

import com.vishwesh.jwt_authentication_series_01.entity.RefreshTokenEntity;
import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;
import com.vishwesh.jwt_authentication_series_01.exception.ApiException;
import com.vishwesh.jwt_authentication_series_01.repository.RefreshTokenRepository;
import com.vishwesh.jwt_authentication_series_01.security.JwtService;
import com.vishwesh.jwt_authentication_series_01.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import java.util.HexFormat;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenServiceImpl(
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository
    ){
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String createRefreshToken(UserEntity user) {
        try {
            String rawToken = jwtService.generateRefreshToken();

            String refreshToken = hashToken(rawToken);

            LocalDateTime expiresAt =
                    LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);

            RefreshTokenEntity refreshTokenEntity =
                    RefreshTokenEntity.builder()
                            .tokenHash(refreshToken)
                            .user(user)
                            .expiresAt(expiresAt)
                            .revoked(false)
                            .build();

            refreshTokenRepository.save(refreshTokenEntity);

            return rawToken;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<UserEntity> validateRefreshToken(String refreshToken) {
        String tokenHash = hashToken(refreshToken);

        RefreshTokenEntity storedToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid refresh token"
                        )
                );

        if (storedToken.isRevoked()) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token has been revoked"
            );
        }

        if (storedToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token has expired"
            );
        }

//        return storedToken.getUser();
        return Optional.of(storedToken.getUser());
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {

    }

    @Override
    public void revokeAllUserTokens(UserEntity user) {

    }


    //
    public String hashToken(String rawToken){
        try{

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e
            );
        }
    }
}
