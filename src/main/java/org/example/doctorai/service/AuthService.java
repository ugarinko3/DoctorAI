package org.example.doctorai.service;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.doctorai.exception.EntityAlreadyExistException;
import org.example.doctorai.model.request.UserRequestAuthorization;
import org.example.doctorai.model.request.UserRequestRegistration;
import org.springframework.security.authentication.AuthenticationManager;
import org.example.doctorai.model.entity.User;
import org.example.doctorai.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    /**
     * Регистрация
     *
     * @param token JWT
     * @return UUID пользователя
     */
    @Transactional
    public UUID registerUser(String token) {

        UserRequestRegistration userRequestRegistration = jwtService.getLoginAndEmailAndPassword(token);
        if (userRepository.existsByEmail(userRequestRegistration.getEmail())) {
            throw new EntityAlreadyExistException("Email уже привязан к учетной записи");
        } else if (userRepository.existsByLogin(userRequestRegistration.getLogin())) {
            throw new EntityAlreadyExistException("Логин уже занят.");
        } else {
                User user = User.builder()
                        .login(userRequestRegistration.getLogin())
                        .password(passwordEncoder.encode(userRequestRegistration.getPassword()))
                        .email(userRequestRegistration.getEmail())
                        .build();
                userRepository.save(user);
                return user.getId();
            }
        }

    /**
     * Авторизация
     *
     * @param token JWT
     * @return UUID пользователя
     */
    @Transactional
    public String authorization(String token) {
        try {
            UserRequestAuthorization userRequestAuthorization = jwtService.getLoginAndPassword(token);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequestAuthorization.getLogin(), userRequestAuthorization.getPassword())
            );
            authenticationManager.authenticate(authentication);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userRequestAuthorization.getLogin());
            return jwtService.generateToken(userDetails.getUsername(), userDetails.getPassword());
        } catch (AuthenticationException e) {
            throw new JwtException("Не удалось авторизоваться");
        }
    }

}
