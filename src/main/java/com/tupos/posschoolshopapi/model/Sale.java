package com.tupos.posschoolshopapi.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"id", "date", "student", "saleDetails", "total", "paymentMethod"})
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @ManyToOne // muchas ventas pueden pertenecer al mismo alumno (puede ser null si paga en efectivo)
    private Student student;

    @OneToMany(cascade = CascadeType.ALL) // una venta tiene muchos SaleDetail. CascadeType.ALL significa que si eliminas la venta, se eliminan sus detalles también
    private List<SaleDetail> saleDetails;

    private Double total;

    @Enumerated(EnumType.STRING) // guarda el enum como texto en PostgreSQL ("CASH" o "PREPAID_BALANCE") en vez de un número
    private PaymentMethod paymentMethod;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public List<SaleDetail> getSaleDetails() { return saleDetails; }
    public void setSaleDetails(List<SaleDetail> saleDetails) { this.saleDetails = saleDetails; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}