package com.tupos.posschoolshopapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@JsonPropertyOrder({"id", "name", "firstName", "paternalLastName", "maternalLastName", "controlNumber", "tutorName", "tutorPhone", "level", "grade", "group", "prepaidBalance", "enrollmentDate"})
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    private String controlNumber;

    private String grade;
    private String level;

    @Column(name = "student_group")
    private String group;

    private String tutorName;
    private String tutorPhone;

    private Double prepaidBalance;

    private LocalDate enrollmentDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("name") // nombre completo calculado, se mantiene como propiedad de solo lectura para no romper todo lo que ya lee "name"
    public String getName() {
        return (firstName + " " + paternalLastName + " " + maternalLastName).trim().replaceAll("\\s+", " ");
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPaternalLastName() { return paternalLastName; }
    public void setPaternalLastName(String paternalLastName) { this.paternalLastName = paternalLastName; }

    public String getMaternalLastName() { return maternalLastName; }
    public void setMaternalLastName(String maternalLastName) { this.maternalLastName = maternalLastName; }

    public String getControlNumber() { return controlNumber; }
    public void setControlNumber(String controlNumber) { this.controlNumber = controlNumber; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public String getTutorPhone() { return tutorPhone; }
    public void setTutorPhone(String tutorPhone) { this.tutorPhone = tutorPhone; }

    public Double getPrepaidBalance() { return prepaidBalance; }
    public void setPrepaidBalance(Double prepaidBalance) { this.prepaidBalance = prepaidBalance; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
