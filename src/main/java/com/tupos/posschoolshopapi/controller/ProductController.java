package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.exception.BadRequestException;
import com.tupos.posschoolshopapi.model.Product;
import com.tupos.posschoolshopapi.repository.ProductRepository;
import com.tupos.posschoolshopapi.service.ActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    public ProductController(ProductRepository productRepository, ActivityLogService activityLogService) {
        this.productRepository = productRepository;
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        activityLogService.log("Alta de producto: " + saved.getName(), null);
        return saved;
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product updated) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());
        product.setBarcode(updated.getBarcode());
        product.setImageUrl(updated.getImageUrl());
        Product saved = productRepository.save(product);
        activityLogService.log("Edición de producto: " + saved.getName(), null);
        return saved;
    }

    @PostMapping("/{id}/stock")
    public Product addStock(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Product product = productRepository.findById(id).orElseThrow();
        Integer amount = Integer.valueOf(request.get("amount").toString());
        if (amount <= 0) {
            throw new BadRequestException("La cantidad a agregar debe ser mayor a 0.");
        }
        product.setStock(product.getStock() + amount);
        Product saved = productRepository.save(product);
        activityLogService.log("Reabastecimiento de stock: " + saved.getName() + " (+" + amount + ")", null);
        return saved;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        activityLogService.log("Baja de producto: " + product.getName(), null);
        productRepository.deleteById(id);
    }
}