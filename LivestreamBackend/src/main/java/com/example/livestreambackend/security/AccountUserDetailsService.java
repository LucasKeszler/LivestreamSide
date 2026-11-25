package com.example.livestreambackend.security;

import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.repository.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bindeleddet mellem Spring Security og vores Account-tabel.
 * Spring Security kalder loadUserByUsername ved login og tjekker selv adgangskoden
 * mod hashen med PasswordEncoder-beanen fra PasswordConfig.
 */
@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Samme normalisering som ved oprettelse, så " Mads@Example.com " også kan logge ind.
        Account account = accountRepository.findByEmail(Account.normalizeEmail(email))
                // Spring Security omdanner denne til en generel "forkert login"-fejl,
                // så brugeren ikke kan se, om e-mailen findes.
                .orElseThrow(() -> new UsernameNotFoundException("Ukendt bruger"));

        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .roles(account.getRole().name()) // bliver til ROLE_USER / ROLE_ADMIN
                .build();
    }
}
