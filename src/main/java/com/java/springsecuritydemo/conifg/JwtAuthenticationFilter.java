package com.java.springsecuritydemo.conifg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.springsecuritydemo.exception.ErrorMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
//@RequiredArgsConstructor
@Slf4j
@Scope("prototype")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authheader = request.getHeader("Authorization");

//            log.info("token: " + authheader);
            System.out.println("token: "+authheader);
            final String jwt;
            final String userEmail;
            if (authheader == null || !authheader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authheader.substring(7);
            System.out.println("jwt filter: "+jwt);
            userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {
                UserDetails usrDetails = userDetailsService.loadUserByUsername(userEmail);
                log.info("usr :   " + usrDetails);
                Boolean isValid = jwtService.validToken(jwt, usrDetails);

                log.info("isValid :   " + isValid);
                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            usrDetails,
                            usrDetails.getPassword(),
                            usrDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    // Final Object Store in Secutity Context with User Details(usr,pwd)
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
            filterChain.doFilter(request, response);

        } catch (MalformedJwtException | SignatureException e) {

//            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String,String> error = new HashMap();
            error.put("error_message","invalid token");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(),error);
            throw new ErrorMessage("invalid token");
        }
        catch (ExpiredJwtException e) {
//            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String,String> error = new HashMap();
            error.put("error_message","token expired");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(),error);
            throw new ErrorMessage("token expired");
        } catch (java.security.SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
