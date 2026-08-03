package com.vishwesh.jwt_authentication_series_01.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 1. Generate Token with default claims
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        System.out.println(">>> [JWT SERVICE] Starting token generation for: " + userDetails.getUsername());
        System.out.println(">>> [JWT SERVICE] Secret key loaded: " + (secretKey != null && !secretKey.isEmpty()));
        System.out.println(">>> [JWT SERVICE] Expiration ms: " + jwtExpiration);

        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            String token = Jwts.builder()
                    .claims()
                    .add(extraClaims)
                    .subject(userDetails.getUsername())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .and()
                    .signWith(getSigningKey())
                    .compact();

            System.out.println(">>> [JWT SERVICE] Token generated successfully!");
            return token;

        } catch (Exception e) {
            System.err.println(">>> [JWT ERROR] Failed to generate token: " + e.getMessage());
            throw e;
        }
    }

    // 3. Extract Username (Subject) from Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 4. Validate Token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

        System.out.println(">>> [JWT SERVICE] Validating token for " + username + " -> Valid: " + isValid);
        return isValid;
    }

    // 5. Extract Expiration Date
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 6. Generic method to extract claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    // 7. Decode Base64 key into HMAC SHA key
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}