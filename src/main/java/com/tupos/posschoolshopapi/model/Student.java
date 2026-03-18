package com.tupos.posschoolshopapi.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@JsonPropertyOrder({"id", "name", "level", "grade", "group", "prepaidBalance"})
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

    private Double prepaidBalance;

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

    public Double getPrepaidBalance() { return prepaidBalance; }
    public void setPrepaidBalance(Double prepaidBalance) { this.prepaidBalance = prepaidBalance; }
}