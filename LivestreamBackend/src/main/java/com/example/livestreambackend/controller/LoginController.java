package com.example.livestreambackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Viser kun login-siden. Selve login (POST /login) og logout (POST /logout)
 * håndteres af Spring Security, se SecurityConfig.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
