package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.exception.BadRequestException;
import com.tupos.posschoolshopapi.exception.ResourceNotFoundException;
import com.tupos.posschoolshopapi.model.Sale;
import com.tupos.posschoolshopapi.model.SaleDetail;
import com.tupos.posschoolshopapi.model.Product;
import com.tupos.posschoolshopapi.model.Student;
import com.tupos.posschoolshopapi.model.PaymentMethod;
import com.tupos.posschoolshopapi.repository.SaleRepository;
import com.tupos.posschoolshopapi.repository.ProductRepository;
import com.tupos.posschoolshopapi.repository.StudentRepository;
import com.tupos.posschoolshopapi.service.ActivityLogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StudentRepository studentRepository;
    private final ActivityLogService activityLogService;

    public SaleController(SaleRepository saleRepository,
                          ProductRepository productRepository,
                          StudentRepository studentRepository,
                          ActivityLogService activityLogService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.studentRepository = studentRepository;
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Sale> getByStudent(@PathVariable Long studentId) {
        return saleRepository.findByStudent_Id(studentId);
    }

    @PostMapping
    public Sale create(@RequestBody Map<String, Object> request) {

        // obtiene el usuario logueado del token JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String staffUsername = auth != null ? auth.getName() : "desconocido";

        PaymentMethod paymentMethod = PaymentMethod.valueOf((String) request.get("paymentMethod"));

        Sale sale = new Sale();
        sale.setDate(LocalDateTime.now());
        sale.setPaymentMethod(paymentMethod);
        sale.setStaffUsername(staffUsername);

        if (paymentMethod == PaymentMethod.PREPAID_BALANCE || paymentMethod == PaymentMethod.FIADO) {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id: " + studentId));
            if (paymentMethod == PaymentMethod.PREPAID_BALANCE && student.getPrepaidBalance() <= 0) {
                throw new BadRequestException("El alumno no tiene saldo suficiente");
            }
            sale.setStudent(student);
            sale.setStudentName(student.getName());
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        List<SaleDetail> details = new ArrayList<>();
        double total = 0;

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = (Integer) item.get("quantity");

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));

            if (product.getStock() < quantity) {
                throw new BadRequestException("Stock insuficiente para: " + product.getName() + ". Stock disponible: " + product.getStock());
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setSubtotal(product.getPrice() * quantity);
            details.add(detail);

            total += detail.getSubtotal();
        }

        if (paymentMethod == PaymentMethod.PREPAID_BALANCE) {
            Student student = sale.getStudent();
            if (student.getPrepaidBalance() < total) {
                throw new BadRequestException("Saldo insuficiente. Saldo actual: " + student.getPrepaidBalance() + ", Total: " + total);
            }
            student.setPrepaidBalance(student.getPrepaidBalance() - total);
            studentRepository.save(student);
        } else if (paymentMethod == PaymentMethod.FIADO) {
            Student student = sale.getStudent();
            student.setPrepaidBalance(student.getPrepaidBalance() - total);
            studentRepository.save(student);
        }

        sale.setSaleDetails(details);
        sale.setTotal(total);

        Sale saved = saleRepository.save(sale);

        String action = switch (paymentMethod) {
            case CASH -> "Venta en efectivo";
            case CARD -> "Venta en tarjeta";
            case PREPAID_BALANCE -> "Venta tipo saldo al alumno: " + saved.getStudent().getName();
            case FIADO -> "Venta tipo fiado al alumno: " + saved.getStudent().getName();
        };
        activityLogService.log(action, total);

        return saved;
    }
}