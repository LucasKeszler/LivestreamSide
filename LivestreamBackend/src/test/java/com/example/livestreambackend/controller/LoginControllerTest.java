package com.example.livestreambackend.controller;

import com.example.livestreambackend.config.PasswordConfig;
import com.example.livestreambackend.config.SecurityConfig;
import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.model.Role;
import com.example.livestreambackend.repository.AccountRepository;
import com.example.livestreambackend.security.AccountUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tester hele login-flowet gennem Spring Security med rigtig BCrypt,
 * men med et mocket repository, så der ikke kræves en database.
 */
@WebMvcTest(LoginController.class)
@Import({SecurityConfig.class, PasswordConfig.class, AccountUserDetailsService.class})
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        Account account = new Account("mads@example.com", "Mads", passwordEncoder.encode("hemmelig123"), Role.USER);
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByEmail("mads@example.com")).thenReturn(Optional.of(account));
    }

    @Test
    void loginPage_isPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void correctLogin_authenticatesWithUserRole() throws Exception {
        mockMvc.perform(formLogin("/login").userParameter("email").user("mads@example.com").password("hemmelig123"))
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("mads@example.com").withRoles("USER"));
    }

    @Test
    void wrongPassword_andUnknownEmail_giveTheSameError() throws Exception {
        mockMvc.perform(formLogin("/login").userParameter("email").user("mads@example.com").password("forkert"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

        mockMvc.perform(formLogin("/login").userParameter("email").user("findes.ikke@example.com").password("forkert"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void protectedPage_redirectsGuestToLogin() throws Exception {
        // Fx en kommende købsside: alt der ikke er offentligt kræver login.
        mockMvc.perform(get("/purchase/123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void logout_redirectsToLoginWithMessage() throws Exception {
        mockMvc.perform(post("/logout").with(user("mads@example.com")).with(csrf()))
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());
    }
}
