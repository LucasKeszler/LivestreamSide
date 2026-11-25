package com.example.livestreambackend.controller;

import com.example.livestreambackend.dto.EventForm;
import com.example.livestreambackend.exception.EventAlreadyExistsException;
import com.example.livestreambackend.service.EventService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin: se og opret events (user story #16).
 * @PreAuthorize gælder alle metoder i klassen, så kun ADMIN har adgang,
 * uanset hvad der står i SecurityConfig. Se MethodSecurityConfig.
 */
@Controller
@RequestMapping("/admin/events")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String showEvents(Model model) {
        model.addAttribute("form", new EventForm());
        model.addAttribute("events", eventService.findAllEvents());
        return "admin/events";
    }

    @PostMapping
    public String createEvent(@Valid @ModelAttribute("form") EventForm form,
                              BindingResult bindingResult,
                              Model model) {
        if (!bindingResult.hasErrors()) {
            try {
                eventService.createEvent(form.getYear(), form.getTitle(), form.getLocation(), form.getYearPassPrice());
                return "redirect:/admin/events?created";
            } catch (EventAlreadyExistsException e) {
                String field = e.getField() == EventAlreadyExistsException.Field.YEAR ? "year" : "title";
                bindingResult.rejectValue(field, "duplicate", e.getMessage());
            }
        }

        // Vis siden igen med fejlene og listen over eksisterende events.
        model.addAttribute("events", eventService.findAllEvents());
        return "admin/events";
    }
}
