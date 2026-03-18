package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.model.Product;
import com.tupos.posschoolshopapi.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/productos")
    public class ProductController {

        private final ProductRepository productRepository;

        public ProductController(ProductRepository productRepository) {
            this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product crear(@RequestBody Product product) {
        return productRepository.save(product);
    }
}