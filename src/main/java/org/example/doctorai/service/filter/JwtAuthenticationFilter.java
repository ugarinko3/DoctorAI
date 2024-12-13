package org.example.doctorai.service.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.example.doctorai.service.CustomUserDetailsService;
import org.example.doctorai.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final static String HEADER_NAME = "Authorization";
    private final static String HEADER_PREFIX = "Bearer ";

    /**
     * Метод для парсинга токена авторизации при запросе
     *
     * @param request     Запрос с данными фильтрации
     * @param response    ответ после фильтрации
     * @param filterChain цепочка фильтров
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
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