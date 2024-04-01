package com.java.springsecuritydemo.service;

import com.java.springsecuritydemo.conifg.JwtService;
import com.java.springsecuritydemo.exception.ErrorMessage;
import com.java.springsecuritydemo.model.AuthenticationRequest;
import com.java.springsecuritydemo.model.AuthenticationResponse;
import com.java.springsecuritydemo.model.RegisterRequest;
import com.java.springsecuritydemo.model.Role;
import com.java.springsecuritydemo.model.User;
import com.java.springsecuritydemo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    //     @Autowired
    private final UserRepository userRepository;

    //     @Autowired
    private final PasswordEncoder passwordEncoder;

    //     @Autowired
    private final JwtService jwtService;

    //     @Autowired
    private final AuthenticationManager authenticationManager;

    public Object register(RegisterRequest registerRequest) {
        log.info("lkdfj: " + registerRequest);
        User user = User.builder()

                .firstname(registerRequest.getFirstName())
                .lastname(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ADMIN)
//                .username(registerRequest.getUserName())
                .build();
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ErrorMessage("User already exist!please login with existing details ");
        } else {
            User userU = userRepository.insert(user);
            System.out.println("sevice: " + userU);
            return userU;
        }
//        log.info("user: " + user);
//        var jwtToken = jwtService.generateToken(user);
//
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .message("token generated successfully")
//                .build();
    }

    //    public  Employee
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                            authenticationRequest.getPassword())
            );

            var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
            System.out.println("user: " + user);
            var accessToken = jwtService.generateToken(user);
            var refreshToken = jwtService.refreshToken(authenticationRequest.getEmail());

            System.out.println("jwt tokent 2: " + accessToken + "-------------" + refreshToken);
            return AuthenticationResponse.builder()
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .message("getting token succesfully")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("invalid credentials");

        }

    }


}
