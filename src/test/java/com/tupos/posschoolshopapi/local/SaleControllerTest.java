package com.tupos.posschoolshopapi.local;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SaleControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;
    String adminToken;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"admin\", \"password\": \"admin123\" }"))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        adminToken = body.split("\"token\":\"")[1].split("\"")[0];
    }

    @Test
    void obtenerVentas() throws Exception {
        mockMvc.perform(get("/api/sales")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void crearVentaEfectivo() throws Exception {
        MvcResult productResult = mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + adminToken))
                .andReturn();
        String productsBody = productResult.getResponse().getContentAsString();
        String productId = productsBody.split("\"id\":")[1].split(",")[0].trim();

        mockMvc.perform(post("/api/sales")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"paymentMethod\": \"CASH\", \"items\": [{ \"productId\": " + productId + ", \"quantity\": 1 }] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.paymentMethod").value("CASH"));
    }

    @Test
    void crearVentaSaldoPrepagado() throws Exception {
        MvcResult productResult = mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + adminToken))
                .andReturn();
        String productsBody = productResult.getResponse().getContentAsString();
        String productId = productsBody.split("\"id\":")[1].split(",")[0].trim();

        MvcResult studentResult = mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + adminToken))
                .andReturn();
        String studentsBody = studentResult.getResponse().getContentAsString();
        String studentId = studentsBody.split("\"id\":")[1].split(",")[0].trim();

        mockMvc.perform(post("/api/sales")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"paymentMethod\": \"PREPAID_BALANCE\", \"studentId\": " + studentId + ", \"items\": [{ \"productId\": " + productId + ", \"quantity\": 1 }] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod").value("PREPAID_BALANCE"));
    }
}