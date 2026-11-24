package com.example.livestreambackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data fra "Opret konto"-formularen. Bruges kun i web-laget;
 * servicen modtager almindelige værdier, så den ikke kender til formularen.
 */
public class RegistrationForm {

    @NotBlank(message = "Navn skal udfyldes")
    @Size(max = 100, message = "Navnet må højst være 100 tegn")
    private String name;

    @NotBlank(message = "E-mail skal udfyldes")
    @Email(message = "Ugyldig e-mail")
    @Size(max = 255, message = "E-mailen må højst være 255 tegn")
    private String email;

    @NotBlank(message = "Adgangskode skal udfyldes")
    @Size(min = 8, max = 72, message = "Adgangskoden skal være mellem 8 og 72 tegn")
    private String password;

    @NotBlank(message = "Gentag adgangskoden")
    private String confirmPassword;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
