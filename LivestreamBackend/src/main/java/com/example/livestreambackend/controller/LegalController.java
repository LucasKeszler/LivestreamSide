package com.example.livestreambackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Public pages with the terms of sale and the privacy policy.
 * Both are open to guests (see SecurityConfig), so anyone can read them before buying.
 * The company details on the pages come from CompanyInfoAdvice.
 */
@Controller
public class LegalController {

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}
