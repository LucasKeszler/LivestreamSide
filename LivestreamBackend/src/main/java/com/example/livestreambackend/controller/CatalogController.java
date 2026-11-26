package com.example.livestreambackend.controller;

import com.example.livestreambackend.service.VideoCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forsiden: videokataloget grupperet pr. år (wireframe 1).
 * "/" er offentlig i SecurityConfig, så gæster kan se kataloget uden at logge ind.
 * Brugere sendes også hertil efter login.
 */
@Controller
public class CatalogController {

    private final VideoCatalogService videoCatalogService;

    public CatalogController(VideoCatalogService videoCatalogService) {
        this.videoCatalogService = videoCatalogService;
    }

    @GetMapping("/")
    public String showCatalog(Model model) {
        model.addAttribute("groups", videoCatalogService.getVideosGroupedByYear());
        return "index";
    }
}
