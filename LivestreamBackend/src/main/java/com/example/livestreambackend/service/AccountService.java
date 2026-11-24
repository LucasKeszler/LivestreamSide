package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.EmailAlreadyInUseException;
import com.example.livestreambackend.model.Account;

/**
 * Kontrakten for alt, der har med konti at gøre.
 * Controllere og andre features afhænger kun af dette interface, ikke af implementeringen,
 * så implementeringen kan skiftes eller mockes uden at resten af koden ændres.
 */
public interface AccountService {

    /**
     * Opretter en ny konto med rollen USER.
     *
     * @param name        brugerens navn
     * @param email       e-mail (normaliseres til små bogstaver uden mellemrum)
     * @param rawPassword adgangskoden i klartekst; den gemmes kun som hash
     * @return den gemte konto
     * @throws EmailAlreadyInUseException hvis e-mailen allerede er i brug
     */
    Account register(String name, String email, String rawPassword);
}
