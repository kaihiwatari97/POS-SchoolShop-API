package com.tupos.posschoolshopapi.repository;

import com.tupos.posschoolshopapi.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}