package com.tupos.posschoolshopapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // le dice a Spring que esta clase tiene configuración global
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // aplica a todos los endpoints que empiecen con /api/
                .allowedOrigins("*") // permite peticiones de cualquier origen
                .allowedMethods("GET", "POST", "PUT", "DELETE"); // métodos permitidos
    }
}