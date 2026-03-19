package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.model.StaffRole;
import com.tupos.posschoolshopapi.model.StaffUser;
import com.tupos.posschoolshopapi.repository.StaffUserRepository;
import com.tupos.posschoolshopapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StaffUserRepository staffUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(StaffUserRepository staffUserRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.staffUserRepository = staffUserRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // endpoint de login — recibe username y password, devuelve token
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        StaffUser user = staffUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // verifica que la contraseña coincida con el hash guardado en la base de datos
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(username, user.getRole().name());

        return Map.of(
                "token", token,
                "role", user.getRole().name(),
                "username", username
        );
    }

    // endpoint temporal para crear el primer admin — lo eliminaremos después
    @PostMapping("/setup")
    public Map<String, String> setup(@RequestBody Map<String, String> request) {
        if (staffUserRepository.count() > 0) {
            throw new RuntimeException("Setup ya fue ejecutado");
        }

        StaffUser admin = new StaffUser();
        admin.setUsername(request.get("username"));
        admin.setPassword(passwordEncoder.encode(request.get("password"))); // encripta la contraseña
        admin.setRole(StaffRole.ADMIN);
        staffUserRepository.save(admin);

        return Map.of("message", "Admin creado correctamente");
    }
}