package com.example.jwt.config;

import com.example.jwt.Repository.UserRepository;
import com.example.jwt.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        username = jwtService.extractUsername(token);
        //Use of the second condition in the if condition
        // to prevent spring security from authenticating an already authenticated user
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userRepository.findByUsername(username)
                    .map(u -> org.springframework.security.core.userdetails.User
                            .withUsername(u.getUsername())
                            .password(u.getPassword())
                            .roles(u.getRole())
                            .build())
                    .orElse(null);
            /*
                UsernamePasswordAuthenticationToken is implementation of Authentication in Spring Security
                We create it using user(who is authenticated), null(we don't need credentials when we already have token), role(roles/permission)
                Then we set it to SpringContext.

                This tells SpringSecurity that given user is authenticated
                After this, any controller method or security check (like @PreAuthorize("hasRole('ADMIN')"))
                will work because the framework knows who the user is.
             */
            if (user != null && jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
