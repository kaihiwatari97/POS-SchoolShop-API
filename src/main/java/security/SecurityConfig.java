package com.tupos.posschoolshopapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // activa Spring Security en la aplicación
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt es el algoritmo estándar para encriptar contraseñas
        // nunca se guarda la contraseña en texto plano
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desactiva CSRF porque usamos JWT, no cookies de sesión
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // sin sesiones — cada petición se autentica con su token
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // login es público
                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/sales").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/students/**").hasRole("ADMIN") // crear/editar/eliminar alumnos solo admin
                        .requestMatchers("/api/products/**").hasRole("ADMIN") // crear/editar/eliminar productos solo admin
                        .requestMatchers("/api/sales/**").hasRole("ADMIN") // ver reportes solo admin
                        .anyRequest().hasRole("ADMIN") // todo lo demás solo admin
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // agrega el JwtFilter antes del filtro de autenticación de Spring para que procese el token primero

        return http.build();
    }
}