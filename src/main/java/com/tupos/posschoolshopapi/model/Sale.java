package com.tupos.posschoolshopapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"id", "date", "staffUsername", "studentId", "student", "saleDetails", "total", "paymentMethod"})
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private String staffUsername; // usuario del sistema que realizó la venta

    @JsonIgnore
    @ManyToOne
    private Student student;

    // conserva el nombre del alumno aunque luego se elimine (ej: al graduarse), para no perder el historial
    private String studentName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SaleDetail> saleDetails;

    private Double total;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @JsonProperty("student")
    public String getStudentDisplay() {
        if (student != null) return student.getName();
        if (studentName != null) return studentName; // alumno ya eliminado (ej: graduado), se conserva el nombre
        return paymentMethod == PaymentMethod.CARD ? "Venta con tarjeta" : "Venta en efectivo";
    }

    @JsonProperty("studentId")
    public Long getStudentId() {
        return student == null ? null : student.getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getStaffUsername() { return staffUsername; }
    public void setStaffUsername(String staffUsername) { this.staffUsername = staffUsername; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public List<SaleDetail> getSaleDetails() { return saleDetails; }
    public void setSaleDetails(List<SaleDetail> saleDetails) { this.saleDetails = saleDetails; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}