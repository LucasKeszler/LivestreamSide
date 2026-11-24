package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.EmailAlreadyInUseException;
import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.model.Role;
import com.example.livestreambackend.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        accountService = new AccountServiceImpl(accountRepository, passwordEncoder);

        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(accountRepository.saveAndFlush(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void register_savesUserWithHashedPasswordAndUserRole() {
        Account account = accountService.register(" Mads ", "  Mads@Example.COM ", "hemmelig123");

        assertThat(account.getEmail()).isEqualTo("mads@example.com");
        assertThat(account.getName()).isEqualTo("Mads");
        assertThat(account.getPasswordHash()).isEqualTo("hashed");
        assertThat(account.getRole()).isEqualTo(Role.USER);
        verify(passwordEncoder).encode("hemmelig123");
    }

    @Test
    void register_checksDuplicateWithNormalizedEmail() {
        when(accountRepository.existsByEmail("mads@example.com")).thenReturn(true);

        assertThatThrownBy(() -> accountService.register("Mads", "MADS@example.com", "hemmelig123"))
                .isInstanceOf(EmailAlreadyInUseException.class);
        verify(accountRepository, never()).saveAndFlush(any());
    }

    @Test
    void register_translatesUniqueConstraintViolation() {
        when(accountRepository.saveAndFlush(any(Account.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> accountService.register("Mads", "mads@example.com", "hemmelig123"))
                .isInstanceOf(EmailAlreadyInUseException.class);
    }
}
