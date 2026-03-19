package com.tupos.posschoolshopapi.controller;

import com.cloudinary.Cloudinary;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class CloudinaryController {

    private final Cloudinary cloudinary;

    public CloudinaryController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @PostMapping
    public Map uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        // sube el archivo a Cloudinary y devuelve la URL de la imagen
        return cloudinary.uploader().upload(file.getBytes(), Map.of("folder", "pos-schoolshop"));
    }
}