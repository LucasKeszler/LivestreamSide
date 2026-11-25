package com.example.livestreambackend.controller;

import com.example.livestreambackend.config.SecurityConfig;
import com.example.livestreambackend.dto.VideoSearchResult;
import com.example.livestreambackend.service.VideoSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoSearchController.class)
@Import(SecurityConfig.class)
class VideoSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // The controller is tested on its own: the service is a mock, so no database is needed.
    @MockitoBean
    private VideoSearchService videoSearchService;

    private VideoSearchResult klimadebat() {
        return new VideoSearchResult(1L, "Klimadebat", 2026, "Folkemødet 2026",
                List.of("Mette Hansen"), new BigDecimal("49.00"), true);
    }

    @Test
    void search_isPublicForGuests() throws Exception {
        when(videoSearchService.search(null, null)).thenReturn(List.of(klimadebat()));

        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"));
    }

    @Test
    void search_passesTextAndYearToService_andShowsResults() throws Exception {
        when(videoSearchService.search("klima", 2026)).thenReturn(List.of(klimadebat()));
        when(videoSearchService.getAvailableYears()).thenReturn(List.of(2026, 2025));

        mockMvc.perform(get("/search").param("q", "klima").param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("q", "klima"))
                .andExpect(model().attribute("year", 2026))
                .andExpect(content().string(containsString("Klimadebat")))
                .andExpect(content().string(containsString("Mette Hansen")));

        verify(videoSearchService).search("klima", 2026);
    }

    @Test
    void emptyYear_meansAllYears() throws Exception {
        mockMvc.perform(get("/search").param("q", "klima").param("year", ""))
                .andExpect(status().isOk());

        verify(videoSearchService).search("klima", null);
    }

    @Test
    void noResults_showsClearMessage() throws Exception {
        when(videoSearchService.search("findes ikke", null)).thenReturn(List.of());

        mockMvc.perform(get("/search").param("q", "findes ikke"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ingen videoer matcher din søgning")));
    }
}
