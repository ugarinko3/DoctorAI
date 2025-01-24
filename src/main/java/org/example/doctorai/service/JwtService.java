package org.example.doctorai.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.example.doctorai.model.request.UserRequest;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private SecretKey secret;

    public JwtService() {
        byte[] keyBytes = "2f73bb18fdf365a62cad45d8841f135dcbd6fbb1dcf5311b6240d96cde65f764".getBytes(StandardCharsets.UTF_8);
        this.secret = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }

    public String generateToken(String email, String password, String login) {
        long expirationTime = 3600000; // 1 час
        return Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .claim("password", password)
                .claim("login", login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public UserRequest getEmailAndPassword(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.get("email", String.class);
        String password = claims.get("password", String.class);
        String login = claims.get("login", String.class);

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        userRequest.setPassword(password);
        userRequest.setLogin(login);
        return userRequest;
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT token is invalid: " + e.getMessage());
            return false;
        }
    }
}