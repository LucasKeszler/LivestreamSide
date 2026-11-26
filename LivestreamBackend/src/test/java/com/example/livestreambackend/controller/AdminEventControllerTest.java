package com.example.livestreambackend.controller;

import com.example.livestreambackend.config.MethodSecurityConfig;
import com.example.livestreambackend.config.SecurityConfig;
import com.example.livestreambackend.exception.EventAlreadyExistsException;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminEventController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class})
class AdminEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @BeforeEach
    void setUp() {
        when(eventService.findAllEvents())
                .thenReturn(List.of(new Event("Folkemødet 2025", new BigDecimal("299.00"), "Allinge", 2025)));
    }

    // --- Adgang ---

    @Test
    void guest_isRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void normalUser_isForbidden() throws Exception {
        mockMvc.perform(get("/admin/events").with(user("kunde@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void normalUser_cannotCreateEvent() throws Exception {
        mockMvc.perform(validPost().with(user("kunde@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        verify(eventService, never()).createEvent(anyInt(), any(), any(), any());
    }

    // --- Admin ---

    @Test
    void admin_seesFormAndExistingEvents() throws Exception {
        mockMvc.perform(get("/admin/events").with(admin()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("form", "events"))
                .andExpect(content().string(containsString("Folkemødet 2025")))
                .andExpect(content().string(containsString("299,00 kr.")));
    }

    @Test
    void admin_createsEventAndIsRedirected() throws Exception {
        mockMvc.perform(validPost().with(admin()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events?created"));

        verify(eventService).createEvent(2026, "Folkemødet 2026", "Allinge", new BigDecimal("299.00"));
    }

    @Test
    void admin_invalidForm_showsErrors() throws Exception {
        mockMvc.perform(post("/admin/events").with(admin()).with(csrf())
                        .param("year", "")
                        .param("title", "")
                        .param("location", "")
                        .param("yearPassPrice", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeHasFieldErrors("form", "year", "title", "location", "yearPassPrice"));

        verify(eventService, never()).createEvent(anyInt(), any(), any(), any());
    }

    @Test
    void admin_duplicateYear_showsErrorOnYear() throws Exception {
        when(eventService.createEvent(anyInt(), any(), any(), any()))
                .thenThrow(new EventAlreadyExistsException(EventAlreadyExistsException.Field.YEAR, "Findes allerede"));

        mockMvc.perform(validPost().with(admin()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "year"));
    }

    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder validPost() {
        return post("/admin/events").with(csrf())
                .param("year", "2026")
                .param("title", "Folkemødet 2026")
                .param("location", "Allinge")
                .param("yearPassPrice", "299.00");
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor admin() {
        return user("admin@example.com").roles("ADMIN");
    }
}
