package com.tupos.posschoolshopapi.controller;

import com.tupos.posschoolshopapi.exception.BadRequestException;
import com.tupos.posschoolshopapi.model.Sale;
import com.tupos.posschoolshopapi.model.Student;
import com.tupos.posschoolshopapi.repository.SaleRepository;
import com.tupos.posschoolshopapi.repository.StudentRepository;
import com.tupos.posschoolshopapi.service.ActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController // le dice a Spring que esta clase maneja peticiones HTTP y devuelve JSON
@RequestMapping("/api/students") // todas las rutas de esta clase empiezan con /api/students
public class StudentController {

    private final StudentRepository studentRepository;
    private final SaleRepository saleRepository;
    private final ActivityLogService activityLogService;

    public StudentController(StudentRepository studentRepository, SaleRepository saleRepository, ActivityLogService activityLogService) {
        this.studentRepository = studentRepository; // Spring inyecta el repositorio automáticamente
        this.saleRepository = saleRepository;
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

    private static final Pattern CONTROL_NUMBER_PATTERN = Pattern.compile("^\\d{8}$");

    private void validateStudent(Student student, Long excludeId) {
        if (student.getFirstName() == null || student.getFirstName().isBlank()) {
            throw new BadRequestException("El nombre(s) es obligatorio.");
        }
        if (student.getPaternalLastName() == null || student.getPaternalLastName().isBlank()) {
            throw new BadRequestException("El apellido paterno es obligatorio.");
        }
        if (student.getMaternalLastName() == null || student.getMaternalLastName().isBlank()) {
            throw new BadRequestException("El apellido materno es obligatorio.");
        }
        if (student.getControlNumber() == null || !CONTROL_NUMBER_PATTERN.matcher(student.getControlNumber()).matches()) {
            throw new BadRequestException("El número de control debe tener exactamente 8 dígitos.");
        }

        Optional<Student> existing = studentRepository.findByControlNumber(student.getControlNumber());
        if (existing.isPresent() && !existing.get().getId().equals(excludeId)) {
            throw new BadRequestException("Ya existe un alumno con el número de control " + student.getControlNumber() + ".");
        }
    }

    @PostMapping // responde a POST /api/students — crea un alumno nuevo
    public Student create(@RequestBody Student student) {
        validateStudent(student, null);
        student.setEnrollmentDate(LocalDate.now());
        student.setPrepaidBalance(0.0); // el saldo siempre nace en 0, se carga aparte
        Student saved = studentRepository.save(student);
        activityLogService.log("Alta de alumno: " + saved.getName(), null);
        return saved;
    }

    @PutMapping("/{id}") // responde a PUT /api/students/1 — edita un alumno existente
    public Student update(@PathVariable Long id, @RequestBody Student updated) {
        Student student = studentRepository.findById(id).orElseThrow();
        validateStudent(updated, id);
        student.setFirstName(updated.getFirstName());
        student.setPaternalLastName(updated.getPaternalLastName());
        student.setMaternalLastName(updated.getMaternalLastName());
        student.setControlNumber(updated.getControlNumber());
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
        if (student.getPrepaidBalance() < 0) {
            throw new BadRequestException("No se puede eliminar al alumno " + student.getName() + " porque tiene saldo negativo ($" + student.getPrepaidBalance() + "). Debe regularizar el saldo antes de darlo de baja.");
        }

        // desvincula las ventas del alumno (conservando su nombre) para no perder el historial al graduarse/eliminarse
        List<Sale> sales = saleRepository.findByStudent_Id(id);
        for (Sale sale : sales) {
            sale.setStudentName(student.getName());
            sale.setStudent(null);
        }
        saleRepository.saveAll(sales);

        activityLogService.log("Baja de alumno: " + student.getName(), null);
        studentRepository.deleteById(id);
    }
}