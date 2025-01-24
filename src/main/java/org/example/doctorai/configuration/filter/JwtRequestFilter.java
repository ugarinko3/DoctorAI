package org.example.doctorai.configuration.filter;

import javax.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;

import lombok.RequiredArgsConstructor;
import org.example.doctorai.service.CustomUserDetailsService;
import org.example.doctorai.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final static String HEADER_NAME = "Authorization";
    private final static String HEADER_PREFIX = "Bearer ";

    /**
     * Метод для парсинга токена авторизации при запросе
     *
     * @param request     Запрос с данными фильтрации
     * @param response    Ответ после фильтрации
     * @param filterChain Цепочка фильтров
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {
        String authorizationHeader = request.getHeader(HEADER_NAME);

        String jwt = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_PREFIX)) {
            jwt = authorizationHeader.substring(7);
            username = jwtService.extractEmail(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}