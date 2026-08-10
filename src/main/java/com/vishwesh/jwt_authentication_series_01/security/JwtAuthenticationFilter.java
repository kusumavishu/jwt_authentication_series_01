package com.vishwesh.jwt_authentication_series_01.security;

import com.vishwesh.jwt_authentication_series_01.service.RefreshTokenService;
import com.vishwesh.jwt_authentication_series_01.entity.UserEntity;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final RefreshTokenService refreshTokenService;


    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;

        this.refreshTokenService = refreshTokenService;
    };

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) throws ServletException {
        String path = request.getServletPath();

        return path.equals("/auth/login") ||
                path.equals("/auth/refresh");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String accessToken;
        final String userEmail;

        // 1. Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract JWT token from header
        accessToken = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(accessToken);

            if (userEmail == null) {
                sendErrorResponse(
                        response,
                        "Invalid access token"
                );
                return;
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);


            if (!jwtService.isTokenValid(
                    accessToken,
                    userDetails
            )) {
                sendErrorResponse(
                        response,
                        "Invalid access token"
                );
                return;
            }

            //
            String refreshToken = request.getHeader("X-Refresh-Token");

            if (refreshToken == null || refreshToken.isBlank()) {
                sendErrorResponse(
                        response,
                        "Refresh token is required"
                );
                return;
            }

            Optional<UserEntity> refreshUser = refreshTokenService.validateRefreshToken(refreshToken);

            if (refreshUser.isEmpty()) {
                sendErrorResponse(
                        response,
                        "Invalid refresh token"
                );
                return;
            }

            if (!refreshUser.get().getEmail().equals(userEmail)) {
                sendErrorResponse(
                        response,
                        "Token user mismatch"
                );
                return;
            }



            // 3. Process if userEmail exists and request is not already authenticated
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // 6. Set Authentication in SecurityContext
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
            // Continue filter chain
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(
                    response,
                    "Access token has expired"
            );
//            throw new RuntimeException(e);
        } catch (JwtException e) {
            sendErrorResponse(
                    response,
                    "Invalid access token"
            );
//            throw new RuntimeException(e);
        } catch (Exception e) {
            sendErrorResponse(
                    response,
                    "Authentication failed"
            );
//            throw new RuntimeException(e);
        }
    }


    private void sendErrorResponse(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter()
                .write("""
                    {
                        "status": 401,
                        "message": "%s"
                    }"""
                .formatted(message));
    }
}