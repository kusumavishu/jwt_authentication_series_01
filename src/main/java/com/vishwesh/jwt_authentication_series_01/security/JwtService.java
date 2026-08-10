package com.vishwesh.jwt_authentication_series_01.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String ISSUER = "jwt-authentication-series";
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;


    // =========================================================
    // ACCESS TOKEN
    // =========================================================
    public String generateAccessToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();

            // claims.put("userId", ...);
            // claims.put("role", ...);

            String accessToken = buildAccessToken(claims, userDetails);
            System.out.println(">>> [JWT] Access token generated successfully for: " + userDetails.getUsername());
            return accessToken;
        } catch (Exception e) {
            System.err.println(">>> [JWT ERROR] Failed to generate access token for: " + userDetails.getUsername());
            System.err.println(">>> [JWT ERROR] Reason: " + e.getMessage());

            throw new IllegalStateException("Failed to generate access token", e);
        }
    }


    // =========================================================
    // ACCESS TOKEN BUILDER
    // =========================================================
    private String buildAccessToken(
            Map<String, Object> claims,
            UserDetails userDetails
    ) {
        try {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + accessTokenExpiration);

            return Jwts.builder()
                    .claims(claims)
                    .subject(userDetails.getUsername())
                    .id(UUID.randomUUID().toString())
                    .issuer(ISSUER)
                    .issuedAt(now)
                    .expiration(expiry)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate access token", e);
        }
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    /*
     * Refresh token is NOT a JWT.
     * It is a cryptographically secure random value.
     * Store its HASH in the database.
     */

    public String generateRefreshToken() {
        try {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(randomBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate refresh token", e);
        }
    }


    // =========================================================
    // JWT USERNAME
    // =========================================================
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }


    // =========================================================
    // JWT ID
    // =========================================================
    public String extractTokenId(String token) {
        return extractClaim(token,Claims::getId);
    }


    // =========================================================
    // EXPIRATION
    // =========================================================
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // =========================================================
    // TOKEN VALIDATION
    // =========================================================
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    // =========================================================
    // EXPIRATION CHECK
    // =========================================================
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // =========================================================
    // CLAIM EXTRACTION
    // =========================================================
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    // =========================================================
    // PARSE JWT
    // =========================================================
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================================================
    // SIGNING KEY
    // =========================================================
    private SecretKey getSigningKey() {
        byte[] keyBytes =secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}