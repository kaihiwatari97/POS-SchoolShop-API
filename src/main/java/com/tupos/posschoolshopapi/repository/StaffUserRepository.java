package com.tupos.posschoolshopapi.repository;

import com.tupos.posschoolshopapi.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {

    // busca un usuario por username — lo necesita Spring Security para verificar credenciales
    Optional<StaffUser> findByUsername(String username);
}