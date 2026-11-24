package com.example.livestreambackend.controller;

import com.example.livestreambackend.config.SecurityConfig;
import com.example.livestreambackend.exception.EmailAlreadyInUseException;
import com.example.livestreambackend.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Controlleren testes isoleret: servicen er en mock, så der kræves ingen database.
    @MockitoBean
    private AccountService accountService;

    @Test
    void getRegister_isPublicAndShowsForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    void postValidForm_registersAndRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("name", "Mads")
                        .param("email", "mads@example.com")
                        .param("password", "hemmelig123")
                        .param("confirmPassword", "hemmelig123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(accountService).register("Mads", "mads@example.com", "hemmelig123");
    }

    @Test
    void postInvalidForm_showsErrorsAndDoesNotRegister() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("name", "")
                        .param("email", "ikke-en-email")
                        .param("password", "kort")
                        .param("confirmPassword", "anderledes"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("form", "name", "email", "password", "confirmPassword"));

        verifyNoInteractions(accountService);
    }

    @Test
    void postDuplicateEmail_showsEmailError() throws Exception {
        when(accountService.register(any(), any(), any()))
                .thenThrow(new EmailAlreadyInUseException("mads@example.com"));

        mockMvc.perform(post("/register").with(csrf())
                        .param("name", "Mads")
                        .param("email", "mads@example.com")
                        .param("password", "hemmelig123")
                        .param("confirmPassword", "hemmelig123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "email"));
    }

    @Test
    void postWithoutCsrf_isRejected() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "Mads")
                        .param("email", "mads@example.com")
                        .param("password", "hemmelig123")
                        .param("confirmPassword", "hemmelig123"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(accountService);
    }
}
