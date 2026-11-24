package com.example.livestreambackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Adgangsregler, login og logout.
 *
 * Alt, der ikke står i listen over offentlige sider, kræver login.
 * Køb er derfor automatisk kun for indloggede brugere.
 * Når katalog- og videosider bygges, skal deres URL'er tilføjes til permitAll,
 * så gæster kan se dem.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/error",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        // Ved forkert login sendes brugeren til /login?error med en generel fejlbesked.
                        .failureUrl("/login?error")
                        // Efter login: tilbage til den side, man prøvede at åbne, ellers forsiden.
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}
