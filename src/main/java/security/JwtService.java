package com.tupos.posschoolshopapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service // le dice a Spring que esta clase es un servicio inyectable
public class JwtService {

    // clave secreta para firmar los tokens — en producción esto va en variables de entorno
    private static final String SECRET = "pos-school-shop-secret-key-2026-muy-larga-para-ser-segura";

    // tiempo de expiración del token: 8 horas en milisegundos
    private static final long EXPIRATION = 1000 * 60 * 60 * 8;

    private SecretKey getKey() {
        // convierte el string secreto en una clave criptográfica
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // genera un token JWT con el username y el rol del usuario
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username) // el "dueño" del token
                .claim("role", role) // dato extra que guardamos en el token
                .issuedAt(new Date()) // fecha de creación
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION)) // fecha de expiración
                .signWith(getKey()) // firma el token con la clave secreta
                .compact();
    }

    // extrae todos los datos del token
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey()) // verifica que el token fue firmado con nuestra clave
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // extrae el username del token
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    // extrae el rol del token
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // verifica si el token es válido y no ha expirado
    public boolean isValid(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}