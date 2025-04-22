package com.group5.best3deals.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())  // Enable CORS settings
                .csrf(csrf -> csrf.disable())  // Disable CSRF (since JWT is used)
                .authorizeHttpRequests(authorize -> authorize
                        // Allow access to static resources
                        .requestMatchers(
                                "/admin-portal.html",
                                "/static/**",
                                "/favicon.ico",
                                "/styles.css",
                                "/scripts.js",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // Allow access to authentication endpoints
                        .requestMatchers(
                                "/auth/**",
                                "/users/**",
                                "/upload/**",
                                "/uploads/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "v3/api-docs/**",
                                "/reset-password.html**",
                                "/reset-success.html**",
                                "/reset-failed.html**"
                        ).permitAll()
                        // admin endpoints to ADMIN role restriction
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        // other req only authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless sessions since JWT is used
                )
                .authenticationProvider(authenticationProvider)  // Custom authentication provider (using email)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://172.17.3.115:8080")); // Allow requests from this origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow these HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept")); // Allow these headers
        configuration.setAllowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS configuration to all endpoints
        return source;
    }
}