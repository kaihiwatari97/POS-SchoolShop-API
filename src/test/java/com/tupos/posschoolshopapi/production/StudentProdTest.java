package com.tupos.posschoolshopapi.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class StudentProdTest {

    static final String BASE_URL = "https://aqui-va-tu-url-de-railway.railway.app";
    RestTemplate restTemplate = new RestTemplate();
    String adminToken;

    @BeforeEach
    void setup() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"username\": \"admin\", \"password\": \"admin12345\" }", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/api/auth/login", request, String.class);
        adminToken = response.getBody().split("\"token\":\"")[1].split("\"")[0];
    }

    @Test
    void obtenerAlumnos() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/api/students", HttpMethod.GET, request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("["));
    }

    @Test
    void crearAlumno() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"name\": \"Alumno Prod Test\", \"grade\": 1, \"level\": \"primaria\", \"group\": \"A\", \"prepaidBalance\": 50.0 }", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/api/students", request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Alumno Prod Test"));
    }
}