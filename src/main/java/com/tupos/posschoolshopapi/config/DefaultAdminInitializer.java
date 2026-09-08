package com.tupos.posschoolshopapi.config;

import com.tupos.posschoolshopapi.model.StaffRole;
import com.tupos.posschoolshopapi.model.StaffUser;
import com.tupos.posschoolshopapi.repository.StaffUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// crea un admin por defecto si la base de datos no tiene ningún usuario todavía
@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminInitializer(StaffUserRepository staffUserRepository, PasswordEncoder passwordEncoder) {
        this.staffUserRepository = staffUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (staffUserRepository.count() > 0) {
            return;
        }

        StaffUser admin = new StaffUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin12345"));
        admin.setRole(StaffRole.ADMIN);
        staffUserRepository.save(admin);
    }
}
