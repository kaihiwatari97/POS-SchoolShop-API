package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.model.StaffRole;
import com.tupos.posschoolshopapi.model.StaffUser;
import com.tupos.posschoolshopapi.repository.StaffUserRepository;
import com.tupos.posschoolshopapi.security.JwtService;
import com.tupos.posschoolshopapi.service.ActivityLogService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StaffUserRepository staffUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public AuthController(StaffUserRepository staffUserRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          ActivityLogService activityLogService) {
        this.staffUserRepository = staffUserRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<StaffUser> userOpt = staffUserRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no existe"));
        }

        StaffUser user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta"));
        }

        String token = jwtService.generateToken(username, user.getRole().name());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole().name(),
                "username", username
        ));
    }

    @PostMapping("/setup")
    public Map<String, String> setup(@RequestBody Map<String, String> request) {
        if (staffUserRepository.count() > 0) {
            throw new RuntimeException("Setup ya fue ejecutado");
        }

        StaffUser admin = new StaffUser();
        admin.setUsername(request.get("username"));
        admin.setPassword(passwordEncoder.encode(request.get("password")));
        admin.setRole(StaffRole.ADMIN);
        staffUserRepository.save(admin);

        return Map.of("message", "Admin creado correctamente");
    }

    // solo admins pueden llamar este endpoint — lo protege SecurityConfig
    @GetMapping("/users")
    public List<Map<String, String>> getUsers() {
        return staffUserRepository.findAll().stream()
                .map(u -> Map.of(
                        "id", u.getId().toString(),
                        "username", u.getUsername(),
                        "role", u.getRole().name()
                ))
                .toList();
    }

    @PostMapping("/users")
    public Map<String, String> createUser(@RequestBody Map<String, String> request) {
        if (staffUserRepository.findByUsername(request.get("username")).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        StaffUser user = new StaffUser();
        user.setUsername(request.get("username"));
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setRole(StaffRole.valueOf(request.get("role")));
        staffUserRepository.save(user);

        String rolLabel = user.getRole() == StaffRole.ADMIN ? "administrador" : "empleado";
        activityLogService.log("Alta de " + rolLabel + ": " + user.getUsername(), null);

        return Map.of("message", "Usuario creado correctamente");
    }

    @DeleteMapping("/users/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        StaffUser user = staffUserRepository.findById(id).orElseThrow();
        String rolLabel = user.getRole() == StaffRole.ADMIN ? "administrador" : "empleado";
        activityLogService.log("Baja de " + rolLabel + ": " + user.getUsername(), null);
        staffUserRepository.deleteById(id);
        return Map.of("message", "Usuario eliminado");
    }
}