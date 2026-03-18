package com.tupos.posschoolshopapi.repository;

import com.tupos.posschoolshopapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring genera automáticamente: findAll, findById, save, deleteById, etc.
}