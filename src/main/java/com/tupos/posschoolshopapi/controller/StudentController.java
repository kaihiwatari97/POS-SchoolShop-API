package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.model.Student;
import com.tupos.posschoolshopapi.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // le dice a Spring que esta clase maneja peticiones HTTP y devuelve JSON
@RequestMapping("/api/students") // todas las rutas de esta clase empiezan con /api/students
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository; // Spring inyecta el repositorio automáticamente
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
        return studentRepository.save(student);
    }

    @PutMapping("/{id}") // responde a PUT /api/students/1 — edita un alumno existente
    public Student update(@PathVariable Long id, @RequestBody Student updated) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setName(updated.getName());
        student.setGrade(updated.getGrade());
        student.setLevel(updated.getLevel());
        student.setGroup(updated.getGroup());
        student.setPrepaidBalance(updated.getPrepaidBalance());
        return studentRepository.save(student);
    }

    @DeleteMapping("/{id}") // responde a DELETE /api/students/1 — elimina un alumno
    public void delete(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }
}