package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.EventAlreadyExistsException;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    private EventRepository eventRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventService = new EventServiceImpl(eventRepository);
        when(eventRepository.saveAndFlush(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createEvent_savesTrimmedValues() {
        Event event = eventService.createEvent(2026, "  Folkemødet 2026 ", " Allinge ", new BigDecimal("299.00"));

        assertThat(event.getYear()).isEqualTo(2026);
        assertThat(event.getTitle()).isEqualTo("Folkemødet 2026");
        assertThat(event.getLocation()).isEqualTo("Allinge");
        assertThat(event.getYearPassPrice()).isEqualByComparingTo("299");
    }

    @Test
    void createEvent_rejectsExistingYear() {
        when(eventRepository.existsByYear(2026)).thenReturn(true);

        assertThatThrownBy(() -> eventService.createEvent(2026, "Folkemødet 2026", "Allinge", BigDecimal.TEN))
                .isInstanceOf(EventAlreadyExistsException.class)
                .extracting(e -> ((EventAlreadyExistsException) e).getField())
                .isEqualTo(EventAlreadyExistsException.Field.YEAR);
        verify(eventRepository, never()).saveAndFlush(any());
    }

    @Test
    void createEvent_rejectsExistingTitle() {
        when(eventRepository.existsByTitle("Folkemødet 2026")).thenReturn(true);

        assertThatThrownBy(() -> eventService.createEvent(2027, "Folkemødet 2026", "Allinge", BigDecimal.TEN))
                .isInstanceOf(EventAlreadyExistsException.class)
                .extracting(e -> ((EventAlreadyExistsException) e).getField())
                .isEqualTo(EventAlreadyExistsException.Field.TITLE);
    }

    @Test
    void createEvent_translatesUniqueConstraintViolation() {
        when(eventRepository.saveAndFlush(any(Event.class))).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> eventService.createEvent(2026, "Folkemødet 2026", "Allinge", BigDecimal.TEN))
                .isInstanceOf(EventAlreadyExistsException.class);
    }
}
