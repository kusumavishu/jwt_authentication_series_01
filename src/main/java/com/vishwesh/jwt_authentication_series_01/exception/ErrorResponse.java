package com.vishwesh.jwt_authentication_series_01.exception;

import lombok.*;

import java.time.LocalDateTime;

//@Getter
//@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;
}


/**
 * @Getter
 * @Setter
 *
 * instead of above
 * Use @Data
 *
 * @Data generates:
 *
 * Getters
 * Setters
 * toString()
 * equals()
 * hashCode()
 * RequiredArgsConstructor
 */