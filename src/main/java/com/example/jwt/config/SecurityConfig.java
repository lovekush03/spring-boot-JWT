/*
   Doubts from Below Code
    1. .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    Ans ->  This tells Spring Security not to create or use an HTTP session to store authentication information.
            By default, spring security is stateful, which means it creates Http Session to store authentication information
            It creates an HTTP session (JSESSIONID) after login.
            The user’s authentication object is stored in the session.
            Every subsequent request checks the session to know who the user is.
            But in JWT-based systems, we don’t want sessions. JWT is a stateless authentication mechanism — the token itself carries all the user’s
            info, so no session is needed.

    2. .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            Spring Security has a chain of filters that process every HTTP request before it reaches your controller.
            UsernamePasswordAuthenticationFilter is the default filter that handles form-based login (i.e., username + password submission).
            We add our custom JWT filter before it, using above line we make JWTAuthFilter run first

            If there’s a valid JWT token → the filter authenticates the user.
            If there’s no token → request moves on; the security chain handles it as unauthenticated.
 */
package com.example.jwt.config;

import com.example.jwt.Service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
