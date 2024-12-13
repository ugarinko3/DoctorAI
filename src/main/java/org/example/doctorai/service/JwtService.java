package org.example.doctorai.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.example.doctorai.model.request.UserRequestAuthorization;
import org.example.doctorai.model.request.UserRequestRegistration;
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

    public UserRequestRegistration getLoginAndEmailAndPassword(String token) {
        Claims claims = getClaimsFromToken(token);

        UserRequestRegistration user = new UserRequestRegistration();
        user.setEmail(claims.get("email", String.class));
        user.setLogin(claims.get("login", String.class));
        user.setPassword(claims.get("password", String.class));
        return user;
    }

    public UserRequestAuthorization getLoginAndPassword(String token) {
        Claims claims = getClaimsFromToken(token);

        UserRequestAuthorization user = new UserRequestAuthorization();
        user.setLogin(claims.get("login", String.class));
        user.setPassword(claims.get("password", String.class));
        return user;
    }

    /**
     * Генерация токена
     *
     * @param login Логин пользователя
     * @param password Пароль пользователя
     * @return JWT
     */
    public String generateToken(String login, String password) {
        return Jwts.builder()
                .setSubject(login)
                .claim("login", login)
                .claim("password", password)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // Токен истекает через 1 час
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получение почты с токена
     *
     * @param token
     * @return
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверка на валидацию токена
     *
     * @param token
     * @return
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
