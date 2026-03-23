package com.tupos.posschoolshopapi.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class ProductProdTest {

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
    void obtenerProductos() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/api/products", HttpMethod.GET, request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("["));
    }

    @Test
    void crearProducto() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"name\": \"Producto Prod Test\", \"price\": 10.0, \"stock\": 5 }", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/api/products", request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Producto Prod Test"));
    }

    @Test
    void empleadoNoPuedeCrearProducto() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"name\": \"No Permitido\", \"price\": 10.0, \"stock\": 5 }", headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->
                restTemplate.postForEntity(BASE_URL + "/api/products", request, String.class));

        assertEquals(403, ex.getStatusCode().value());
    }
}