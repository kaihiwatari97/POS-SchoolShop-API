package com.tupos.posschoolshopapi.production;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class AuthProdTest {

    // cambia esta URL cuando tengas Railway
    static final String BASE_URL = "https://aqui-va-tu-url-de-railway.railway.app";
    RestTemplate restTemplate = new RestTemplate();

    @Test
    void loginCorrecto() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"username\": \"admin\", \"password\": \"admin12345\" }", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/api/auth/login", request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("token"));
        assertTrue(response.getBody().contains("ADMIN"));
    }

    @Test
    void loginUsuarioIncorrecto() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"username\": \"noexiste\", \"password\": \"cualquiera\" }", headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restTemplate.postForEntity(BASE_URL + "/api/auth/login", request, String.class));

        assertEquals(401, ex.getStatusCode().value());
        assertTrue(ex.getResponseBodyAsString().contains("Usuario no existe"));
    }

    @Test
    void loginContrasenaIncorrecta() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"username\": \"admin\", \"password\": \"wrongpassword\" }", headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restTemplate.postForEntity(BASE_URL + "/api/auth/login", request, String.class));

        assertEquals(401, ex.getStatusCode().value());
        assertTrue(ex.getResponseBodyAsString().contains("Contraseña incorrecta"));
    }
}