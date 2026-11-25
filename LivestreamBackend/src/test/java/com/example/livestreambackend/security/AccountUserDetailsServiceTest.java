package com.example.livestreambackend.security;

import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.model.Role;
import com.example.livestreambackend.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountUserDetailsServiceTest {

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final AccountUserDetailsService service = new AccountUserDetailsService(accountRepository);

    @Test
    void loadUser_mapsAccountToUserDetailsWithRole() {
        Account admin = new Account("admin@example.com", "Admin", "hash", Role.ADMIN);
        when(accountRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        UserDetails user = service.loadUserByUsername("  Admin@Example.com ");

        assertThat(user.getUsername()).isEqualTo("admin@example.com");
        assertThat(user.getPassword()).isEqualTo("hash");
        assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUser_unknownEmailThrows() {
        when(accountRepository.findByEmail("ukendt@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ukendt@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
