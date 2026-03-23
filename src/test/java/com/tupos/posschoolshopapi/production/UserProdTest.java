package com.tupos.posschoolshopapi.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class UserProdTest {

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
    void adminPuedeVerUsuarios() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/api/auth/users", HttpMethod.GET, request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("["));
    }

    @Test
    void adminPuedeCrearUsuario() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"username\": \"testprod123\", \"password\": \"test12345\", \"role\": \"EMPLOYEE\" }", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/api/auth/users", request, String.class);

        assertEquals(200, response.getStatusCode().value());
    }
}