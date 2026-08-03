package com.vishwesh.jwt_authentication_series_01.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password should contain at least 6 characters")
    private String password;
}
