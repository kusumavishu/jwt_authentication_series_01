package com.vishwesh.jwt_authentication_series_01.repository;

import com.vishwesh.jwt_authentication_series_01.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity,Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
}
