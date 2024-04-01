package com.java.springsecuritydemo.conifg;

import com.java.springsecuritydemo.exception.ErrorMessage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secretkey}")
    private String SECRET_KEY;

    @Value("${jwt.accesstoken}")
    private Integer accessToken;
    @Value("${jwt.refershtoken}")
    private  Integer refreshToken;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);

    }

    public String generateToken(UserDetails userDetails) {
        System.out.println("user" + userDetails);
        return generateToken(new HashMap<>(), userDetails);
    }
//    public String refereshToken(UserDetails userDetails) {
//        System.out.println("user" + userDetails);
//        return generateToken(new HashMap<>(), userDetails);
//    }

    // check token name and database username is valid or not
    public Boolean validToken(String token, UserDetails userDetails) throws SignatureException {
        String tokenUserName = extractUsername(token);
        return (tokenUserName.equals(userDetails.getUsername()) && !isTokenExp(token));
    }

    // Check token Exp
    public boolean isTokenExp(String token) throws SignatureException {

        Date exp = expDate(token);
        return exp.before(new Date(System.currentTimeMillis()));


    }

    // Read Exp Date
    public Date expDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
//                .setIssuer(userDetails.get)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(accessToken)))
                .signWith(SignatureAlgorithm.HS512, getSignKey())
                .compact();
    }
    public String refreshToken(String user) {
        return Jwts.builder()
                .setSubject(user)
//                .setIssuer(userDetails.get)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(refreshToken)))
                .signWith(SignatureAlgorithm.HS512, getSignKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }
}
