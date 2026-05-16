package com.url.shortner.security.jwt;

import com.url.shortner.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // ✅ Fix: long instead of int (prevents overflow)

    // ✅ Fix: "Bearer " with space — correctly extracts token
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) { // ✅ space added
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateToken(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key()) // ✅ Fix: no unsafe cast needed
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // ✅ Fix: Returns SecretKey directly — no unsafe cast needed elsewhere
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // ✅ Fix: Returns false instead of throwing RuntimeException
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("JWT is null or empty: " + e.getMessage());
            return false;
        }
    }
}