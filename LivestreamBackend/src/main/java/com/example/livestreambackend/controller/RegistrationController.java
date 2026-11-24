package com.example.livestreambackend.controller;

import com.example.livestreambackend.dto.RegistrationForm;
import com.example.livestreambackend.exception.EmailAlreadyInUseException;
import com.example.livestreambackend.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final AccountService accountService;

    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult) {
        if (!form.passwordsMatch()) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Adgangskoderne er ikke ens");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            accountService.register(form.getName(), form.getEmail(), form.getPassword());
        } catch (EmailAlreadyInUseException e) {
            bindingResult.rejectValue("email", "duplicate", "Der findes allerede en konto med denne e-mail");
            return "register";
        }

        return "redirect:/login?registered";
    }
}
