package com.java.springsecuritydemo.security;

import java.io.IOException;
import java.io.Serializable;

// import javax.servlet.ServletException;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

import com.java.springsecuritydemo.exception.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class InvalidUserAuthEntryPoint implements AuthenticationEntryPoint, Serializable {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Token");
//        throw new RuntimeException("Unauthorized Token");
        response.sendError(600, "Unauthorized Token");

    }

}
