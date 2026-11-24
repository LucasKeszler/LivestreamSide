package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.EmailAlreadyInUseException;
import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.model.Role;
import com.example.livestreambackend.repository.AccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Account register(String name, String email, String rawPassword) {
        String normalizedEmail = Account.normalizeEmail(email);

        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException(normalizedEmail);
        }

        // Rollen sættes altid her og aldrig fra formularen, så ingen kan oprette sig selv som ADMIN.
        Account account = new Account(normalizedEmail, name.trim(), passwordEncoder.encode(rawPassword), Role.USER);

        try {
            return accountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException e) {
            // To samtidige oprettelser med samme e-mail: UNIQUE-constrainten i databasen fanger den sidste.
            throw new EmailAlreadyInUseException(normalizedEmail);
        }
    }
}
