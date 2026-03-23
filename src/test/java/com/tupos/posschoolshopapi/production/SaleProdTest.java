package com.tupos.posschoolshopapi.production;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class SaleProdTest {

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
    void obtenerVentas() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/api/sales", HttpMethod.GET, request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("["));
    }

    @Test
    void crearVentaEfectivo() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<String> productsResponse = restTemplate.exchange(
                BASE_URL + "/api/products", HttpMethod.GET, getRequest, String.class);
        String productId = productsResponse.getBody().split("\"id\":")[1].split(",")[0].trim();

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{ \"paymentMethod\": \"CASH\", \"items\": [{ \"productId\": " + productId + ", \"quantity\": 1 }] }", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/api/sales", request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("CASH"));
    }
}