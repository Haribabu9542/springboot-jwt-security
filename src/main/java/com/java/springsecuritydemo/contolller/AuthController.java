package com.java.springsecuritydemo.contolller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.springsecuritydemo.conifg.JwtService;
import com.java.springsecuritydemo.exception.ErrorMessage;
import com.java.springsecuritydemo.model.AuthenticationRequest;
import com.java.springsecuritydemo.model.AuthenticationResponse;
import com.java.springsecuritydemo.model.RegisterRequest;
import com.java.springsecuritydemo.model.User;
import com.java.springsecuritydemo.repository.UserRepository;
import com.java.springsecuritydemo.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.cassandra.CassandraInvalidQueryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//@RequiredArgsConstructor
@RestController()
@RequestMapping(path = "/api/v1/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {


        try {
            System.out.println("regist:   " + registerRequest);
            User userU = new User();
            if (bindingResult.hasErrors()) {
                String message = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(","));

                return ResponseEntity.badRequest().body(message);

            } else {
                 User user = (User) authService.register(registerRequest);
                System.out.println("user:   "+user);
                if (user.getEmail() == null && user.getUsername() == null) {
                    System.out.println("inside user:   "+user);

                    throw new ErrorMessage("User already exist!please login with existing details ");
                } else {
                    System.out.println("added");
                    return ResponseEntity.ok(user);

                }
            }
        } catch (CassandraInvalidQueryException e) {
//            e.printStackTrace();
            System.out.println("click");
            throw new ErrorMessage("provide all values");
        }
    }

    @PostMapping("/authentication")
    public ResponseEntity<Object> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest,BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));

            return ResponseEntity.badRequest().body(message);

        } else {
            AuthenticationResponse authenticationResponse = authService.authenticate(authenticationRequest);
            return ResponseEntity.ok(authenticationResponse);
        }
    }


    @PostMapping(path = "/token/refresh")
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        try {
            System.out.println("comminng");
            final String authheader = request.getHeader("Authorization");

//            log.info("token: " + authheader);
            System.out.println("token: " + authheader);
            final String jwt;
            final String userEmail;
            if (authheader == null || !authheader.startsWith("Bearer")) {
//                filterChain.doFilter(request, response);
                return;
            }
            jwt = authheader.substring(7);
            System.out.println("jwt filter687: " + jwt);
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("useremai: " + userEmail);
            System.out.println("security context: " + SecurityContextHolder.getContext()
                    .getAuthentication());
            if (userEmail != null) {
                UserDetails usrDetails = userDetailsService.loadUserByUsername(userEmail);
                log.info("usr :   " + usrDetails);
                Boolean isValid = jwtService.validToken(jwt, usrDetails);

                log.info("isValid :   " + isValid);
                if (isValid) {
                    var accessToken = jwtService.refreshToken(userEmail);
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            usrDetails,
//                            usrDetails.getPassword(),
//                            usrDetails.getAuthorities());
//                    authToken.setDetails(new WebAuthenticationDetailsSource()
//                            .buildDetails(request));
//                    // Final Object Store in Secutity Context with User Details(usr,pwd)
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    AuthenticationResponse authres = AuthenticationResponse.builder()
                            .access_token(accessToken)
                            .refresh_token(jwt)
                            .message("token generated successfully")
                            .build();
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), authres);
                }
            } else {
//            filterChain.doFilter(request, response);
                throw new RuntimeException("refresh token missing");
            }
        } catch (MalformedJwtException | SignatureException e) {

//            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap();
            error.put("error_message", "invalid token");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            throw new ErrorMessage("invalid token");
        } catch (ExpiredJwtException e) {
//            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap();
            error.put("error_message", "token expired");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            throw new ErrorMessage("token expired");
        } catch (java.security.SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}

