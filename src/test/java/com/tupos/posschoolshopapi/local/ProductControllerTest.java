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
class ProductControllerTest {

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
    void obtenerProductos() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void crearProducto() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Producto Test\", \"price\": 10.0, \"stock\": 5 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Producto Test"))
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    void empleadoNoPuedeCrearProducto() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"empleado1\", \"password\": \"empleado123\" }"))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        String empToken = body.split("\"token\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + empToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Producto No Permitido\", \"price\": 10.0, \"stock\": 5 }"))
                .andExpect(status().isForbidden());
    }
}