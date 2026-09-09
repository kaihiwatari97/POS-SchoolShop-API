package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.model.Student;
import com.tupos.posschoolshopapi.repository.StudentRepository;
import com.tupos.posschoolshopapi.service.ActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController // le dice a Spring que esta clase maneja peticiones HTTP y devuelve JSON
@RequestMapping("/api/students") // todas las rutas de esta clase empiezan con /api/students
public class StudentController {

    private final StudentRepository studentRepository;
    private final ActivityLogService activityLogService;

    public StudentController(StudentRepository studentRepository, ActivityLogService activityLogService) {
        this.studentRepository = studentRepository; // Spring inyecta el repositorio automáticamente
        this.activityLogService = activityLogService;
    }

    @GetMapping // responde a GET /api/students — devuelve todos los alumnos
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}") // responde a GET /api/students/1 — devuelve un alumno por ID
    public Student getById(@PathVariable Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    @PostMapping // responde a POST /api/students — crea un alumno nuevo
    public Student create(@RequestBody Student student) {
        student.setEnrollmentDate(LocalDate.now());
        student.setPrepaidBalance(0.0); // el saldo siempre nace en 0, se carga aparte
        Student saved = studentRepository.save(student);
        activityLogService.log("Alta de alumno: " + saved.getName(), null);
        return saved;
    }

    @PutMapping("/{id}") // responde a PUT /api/students/1 — edita un alumno existente
    public Student update(@PathVariable Long id, @RequestBody Student updated) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setName(updated.getName());
        student.setGrade(updated.getGrade());
        student.setLevel(updated.getLevel());
        student.setGroup(updated.getGroup());
        student.setTutorName(updated.getTutorName());
        student.setTutorPhone(updated.getTutorPhone());
        Student saved = studentRepository.save(student);
        activityLogService.log("Edición de alumno: " + saved.getName(), null);
        return saved;
    }

    @PostMapping("/{id}/balance") // responde a POST /api/students/1/balance — carga saldo al alumno
    public Student addBalance(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Student student = studentRepository.findById(id).orElseThrow();
        Double amount = Double.valueOf(request.get("amount").toString());
        student.setPrepaidBalance(student.getPrepaidBalance() + amount);
        Student saved = studentRepository.save(student);
        activityLogService.log("Cargo de saldo al alumno: " + saved.getName(), amount);
        return saved;
    }

    @DeleteMapping("/{id}") // responde a DELETE /api/students/1 — elimina un alumno
    public void delete(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElseThrow();
        activityLogService.log("Baja de alumno: " + student.getName(), null);
        studentRepository.deleteById(id);
    }
}