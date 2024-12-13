package org.example.doctorai.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.doctorai.model.entity.User;
import org.example.doctorai.model.dto.CustomUserDetails;
import org.example.doctorai.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загрузка нового локального пользователя
     *
     * @param email
     * @return {@link UserDetails}
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email, String login) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return new CustomUserDetails(user);
    }

    /**
     * Постоянная сущность пользователя
     *
     * @return {@link User}
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal()
                instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }
        return null;
    }
}
