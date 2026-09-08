package com.tupos.posschoolshopapi.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@JsonPropertyOrder({"id", "name", "tutorName", "tutorPhone", "level", "grade", "group", "prepaidBalance", "enrollmentDate"})
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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