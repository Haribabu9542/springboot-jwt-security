package com.java.springsecuritydemo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Projection for the {@link com.java.springsecuritydemo.model.User} entity
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @Email(message = "email not valid")
    @NotBlank(message = "email not to be null ")
    private String email;
    @NotBlank(message = "password not to be null ")
    private String password;
}