package com.example.livestreambackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Slår @PreAuthorize til, så adgang kan styres direkte på controllere og services,
 * fx @PreAuthorize("hasRole('ADMIN')").
 *
 * Ligger i sin egen klasse, så features kan beskytte deres egne sider
 * uden at alle skal rette i SecurityConfig samtidig.
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
