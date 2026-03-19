package com.tupos.posschoolshopapi.security;

import com.tupos.posschoolshopapi.model.StaffUser;
import com.tupos.posschoolshopapi.repository.StaffUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component // le dice a Spring que cree una instancia de esta clase automáticamente
public class JwtFilter extends OncePerRequestFilter {
    // OncePerRequestFilter garantiza que este filtro se ejecuta una sola vez por petición

    private final JwtService jwtService;
    private final StaffUserRepository staffUserRepository;

    public JwtFilter(JwtService jwtService, StaffUserRepository staffUserRepository) {
        this.jwtService = jwtService;
        this.staffUserRepository = staffUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // lee el header Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // si no hay token o no empieza con "Bearer ", deja pasar la petición sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extrae el token quitando el prefijo "Bearer "
        String token = authHeader.substring(7);

        if (jwtService.isValid(token)) {
            String username = jwtService.getUsername(token);
            String role = jwtService.getRole(token);

            // busca el usuario en la base de datos para confirmar que existe
            StaffUser user = staffUserRepository.findByUsername(username).orElse(null);

            if (user != null) {
                // crea el objeto de autenticación que Spring Security usa internamente
                // ROLE_ es un prefijo requerido por Spring Security
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                // guarda la autenticación en el contexto de seguridad de esta petición
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}