package com.example.livestreambackend.controller;

import com.example.livestreambackend.config.SecurityConfig;
import com.example.livestreambackend.dto.CatalogVideo;
import com.example.livestreambackend.dto.YearVideoGroup;
import com.example.livestreambackend.service.VideoCatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
@Import(SecurityConfig.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Controlleren testes alene: servicen er en mock, så der skal ingen database til.
    @MockitoBean
    private VideoCatalogService videoCatalogService;

    private CatalogVideo video(String title, String speaker) {
        return new CatalogVideo(1L, title, "Beskrivelse", List.of(speaker), 45,
                new BigDecimal("49.00"), null, true);
    }

    private List<YearVideoGroup> twoYears() {
        return List.of(
                new YearVideoGroup(2026, "Folkemødet 2026", "Allinge", new BigDecimal("299.00"),
                        List.of(video("Klimadebat", "Mette Hansen"))),
                new YearVideoGroup(2025, "Folkemødet 2025", "Allinge", new BigDecimal("249.00"),
                        List.of(video("Sundhed for alle", "Ali Khan"))));
    }

    @Test
    void catalog_isPublicForGuests() throws Exception {
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(twoYears());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("groups", hasSize(2)));
    }

    @Test
    @WithMockUser
    void catalog_worksForLoggedInUsers() throws Exception {
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(twoYears());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void showsVideosUnderTheirYear_newestYearFirst() throws Exception {
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(twoYears());

        mockMvc.perform(get("/"))
                .andExpect(content().string(stringContainsInOrder(
                        "Folkemødet 2026", "Klimadebat", "Mette Hansen",
                        "Folkemødet 2025", "Sundhed for alle", "Ali Khan")))
                .andExpect(content().string(containsString("299,00 kr.")))
                .andExpect(content().string(containsString("49,00 kr.")));
    }

    @Test
    void videoCard_showsTitleSpeakersThumbnailAndPrice() throws Exception {
        CatalogVideo klimadebat = new CatalogVideo(1L, "Klimadebat", "Beskrivelse",
                List.of("Ali Khan", "Mette Hansen"), 55, new BigDecimal("39.50"),
                "https://cdn.example.com/klima.jpg", true);
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(List.of(
                new YearVideoGroup(2026, "Folkemødet 2026", "Allinge", new BigDecimal("299.00"),
                        List.of(klimadebat))));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Klimadebat")))
                .andExpect(content().string(containsString("Ali Khan, Mette Hansen")))
                .andExpect(content().string(containsString("src=\"https://cdn.example.com/klima.jpg\"")))
                .andExpect(content().string(containsString("39,50 kr.")));
    }

    @Test
    void videoCard_withoutThumbnail_showsPlaceholder() throws Exception {
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(twoYears());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Intet billede til Klimadebat")));
    }

    @Test
    void yearWithoutVideos_showsMessage() throws Exception {
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(List.of(
                new YearVideoGroup(2026, "Folkemødet 2026", "Allinge", new BigDecimal("299.00"), List.of())));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Der er ikke lagt videoer op fra dette år endnu.")));
    }

    @Test
    void noEvents_showsMessage() throws Exception {
        when(videoCatalogService.getVideosGroupedByYear()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Der er ingen videoer endnu.")));
    }
}
