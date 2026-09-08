package com.tupos.posschoolshopapi.repository;

import com.tupos.posschoolshopapi.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByStudentId(Long studentId);
}