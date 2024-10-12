package com.myproyect.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Maneja la ruta raíz y todas las rutas de la aplicación
    @GetMapping(value = {"/", "/galton-board/*", "/distribucion/", "/fabrica-gaus/*"})
    public String index() {
        return "index"; // Este es el archivo HTML principal en src/main/resources/templates/index.html
    }
}

