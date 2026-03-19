package com.tupos.posschoolshopapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StaffUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // no pueden existir dos usuarios con el mismo username
    private String username;

    private String password; // se va a guardar encriptado, nunca en texto plano

    @Enumerated(EnumType.STRING)
    private StaffRole role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }
}