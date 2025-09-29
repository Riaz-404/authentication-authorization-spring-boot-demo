package com.example.authenticationauthorizationdemo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtils {
    private String secret = "authentication-authorization-demo-project";

    public String generateAccessToken(String email) {
        final long tokenExpire = 3600 * 24; // 1d

        return getJwtToken(email, tokenExpire);
    }

    public String generateRefreshToken(String email) {
        final long tokenExpire = 3600 * 24 * 15; // 15d

        return getJwtToken(email, tokenExpire);
    }

    private String getJwtToken(String email, long tokenExpire) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpire))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public boolean validateToken (String token) {
        try {
            Claims claims = getClaims(token);

            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUserEmailFromToken (String token) {
        try {
            Claims claims = getClaims(token);

            return claims.getSubject();
        } catch (JwtException e) {
            return e.toString();
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
