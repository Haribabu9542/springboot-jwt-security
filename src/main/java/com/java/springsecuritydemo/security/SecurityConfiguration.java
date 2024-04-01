package com.java.springsecuritydemo.security;


import com.java.springsecuritydemo.conifg.JwtAuthenticationFilter;
import com.java.springsecuritydemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    //    @Autowired
    private final UserRepository userRepository;

    //    @Autowired
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    InvalidUserAuthEntryPoint userAuthEntryPoint() {
        return new InvalidUserAuthEntryPoint();
    }


    //    @Autowired
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/register","/api/v1/auth/authentication","/api/v1/auth/refresh")
                .permitAll()
                .anyRequest()
                .authenticated()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(userAuthEntryPoint())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
