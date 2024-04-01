package com.java.springsecuritydemo.model;


import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("user")
public class User implements UserDetails {

//    @PrimaryKeyColumn(name = "email", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @PrimaryKey
    @Email(message = "email not valid")
    private String email;


    @NotBlank(message = "firstName  not to be null ")
    @Size(min = 4, max = 10, message = "firstname must be between 6 and 15 characters")
    @Column("firstname")

    private String firstname;
    @NotBlank(message = "lastName not to be null ")
    @Size(min = 4, max = 10, message = "lastname must be between 6 and 15 characters")

    @Column("lastname")
    private String lastname;

    @Pattern(regexp = "^((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})", message = "user password atleast 6 to 15 charatacters and should contain a-zA-Z0-9@$#%& ")
    @Column("password")
    private String password;

    @Column("role")
    private Role role;
//    @NotBlank(message = "Username must not be null and must contain 6 or more characters")
//    @Size(min = 6, max = 15, message = "username must be between 6 and 15 characters")
//    @PrimaryKeyColumn(name = "username", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
//    @Column("username")
//    private String username;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("role: " + role.name());
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
