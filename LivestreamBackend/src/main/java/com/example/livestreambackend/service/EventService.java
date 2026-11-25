package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.EventAlreadyExistsException;
import com.example.livestreambackend.model.Event;

import java.math.BigDecimal;
import java.util.List;

/**
 * Kontrakten for events (ét Folkemøde pr. år).
 * Controllere og andre features afhænger kun af dette interface.
 */
public interface EventService {

    /**
     * Opretter et nyt event.
     *
     * @throws EventAlreadyExistsException hvis året eller titlen allerede findes
     */
    Event createEvent(int year, String title, String location, BigDecimal yearPassPrice);

    /** Alle events, nyeste år først. */
    List<Event> findAllEvents();
}
