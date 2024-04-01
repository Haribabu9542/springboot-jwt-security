package com.java.springsecuritydemo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Projection for the {@link User} entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
//    @NotBlank(message = "Username must not be null and must contain 6 or more characters")
//    @Size(min = 6, max = 15, message = "username must be between 6 and 15 characters")
//    private String userName;
    @NotBlank(message = "firstName  not to be null ")
    @Size(min = 4, max = 10, message = "firstname must be between 6 and 15 characters")
    private String firstName;

    @NotBlank(message = "lastName not to be null ")
    @Size(min = 4, max = 10, message = "lastname must be between 6 and 15 characters")
    private String lastName;
    @Email(message = "email not valid")
    @NotBlank(message = "email not to be null ")

    private String email;
    @Pattern(regexp = "^((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})", message = "user password atleast 6 to 15 charatacters and should contain a-zA-Z0-9@$#%& ")
    private String password;
}