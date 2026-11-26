package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.EventAlreadyExistsException;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.repository.EventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public Event createEvent(int year, String title, String location, BigDecimal yearPassPrice) {
        String trimmedTitle = title.trim();

        if (eventRepository.existsByYear(year)) {
            throw new EventAlreadyExistsException(EventAlreadyExistsException.Field.YEAR,
                    "Der findes allerede et event for " + year);
        }
        if (eventRepository.existsByTitle(trimmedTitle)) {
            throw new EventAlreadyExistsException(EventAlreadyExistsException.Field.TITLE,
                    "Der findes allerede et event med titlen \"" + trimmedTitle + "\"");
        }

        Event event = new Event(trimmedTitle, yearPassPrice, location.trim(), year);

        try {
            return eventRepository.saveAndFlush(event);
        } catch (DataIntegrityViolationException e) {
            // To admins opretter samme år samtidig: UNIQUE-constrainten i databasen fanger den sidste.
            throw new EventAlreadyExistsException(EventAlreadyExistsException.Field.YEAR,
                    "Der findes allerede et event for " + year);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAllEvents() {
        return eventRepository.findAllByOrderByYearDesc();
    }
}
