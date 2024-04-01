package com.java.springsecuritydemo.contolller;


import com.java.springsecuritydemo.exception.ErrorMessage;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {
    @GetMapping(path = "/hello")
    public ResponseEntity<Object> sayHello() {
        try {
            return ResponseEntity.ok("hello");
        } catch (MalformedJwtException | SignatureException ex) {
            throw new ErrorMessage("invalid token");
        }
    }
}
