package com.tupos.posschoolshopapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice // le dice a Spring que esta clase maneja excepciones de todos los controllers
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // atrapa ResourceNotFoundException en cualquier controller
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // devuelve 404
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class) // atrapa BadRequestException en cualquier controller
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // devuelve 400
                .body(Map.of("error", ex.getMessage()));
    }
}