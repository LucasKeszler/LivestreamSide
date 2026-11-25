package com.example.livestreambackend.service;

import com.example.livestreambackend.dto.VideoSearchResult;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.model.Speaker;
import com.example.livestreambackend.model.Video;
import com.example.livestreambackend.repository.EventRepository;
import com.example.livestreambackend.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VideoSearchServiceImplTest {

    private VideoRepository videoRepository;
    private EventRepository eventRepository;
    private VideoSearchService searchService;

    private Event event2025;
    private Event event2026;

    @BeforeEach
    void setUp() {
        videoRepository = mock(VideoRepository.class);
        eventRepository = mock(EventRepository.class);
        searchService = new VideoSearchServiceImpl(videoRepository, eventRepository);

        event2025 = new Event("Folkemødet 2025", new BigDecimal("249.00"), "Allinge", 2025);
        event2026 = new Event("Folkemødet 2026", new BigDecimal("299.00"), "Allinge", 2026);

        Video klima = video("Klimadebat", event2026, "vid-1", new Speaker("Mette Hansen"));
        Video skole = video("Fremtidens skole", event2026, null, new Speaker("Lars Jensen"));
        Video sundhed = video("Sundhed for alle", event2025, "vid-3",
                new Speaker("Mette Hansen"), new Speaker("Ali Khan"));

        when(videoRepository.findAllBy()).thenReturn(List.of(klima, skole, sundhed));
    }

    private Video video(String title, Event event, String reference, Speaker... speakers) {
        Video video = new Video(title, "Beskrivelse", 45, new BigDecimal("49.00"), null, event, reference);
        for (Speaker speaker : speakers) {
            video.addSpeaker(speaker);
        }
        return video;
    }

    private List<String> titles(List<VideoSearchResult> results) {
        return results.stream().map(VideoSearchResult::getTitle).toList();
    }

    @Test
    void noFilters_returnsAllVideos_newestYearFirstThenByTitle() {
        List<VideoSearchResult> results = searchService.search(null, null);

        assertThat(titles(results)).containsExactly("Fremtidens skole", "Klimadebat", "Sundhed for alle");
    }

    @Test
    void searchesInTitle_ignoringCase() {
        assertThat(titles(searchService.search("KLIMA", null))).containsExactly("Klimadebat");
    }

    @Test
    void searchesInSpeakerName() {
        assertThat(titles(searchService.search("mette", null)))
                .containsExactly("Klimadebat", "Sundhed for alle");
    }

    @Test
    void filtersOnYear() {
        assertThat(titles(searchService.search(null, 2025))).containsExactly("Sundhed for alle");
    }

    @Test
    void combinesTextAndYear() {
        assertThat(titles(searchService.search("mette", 2026))).containsExactly("Klimadebat");
    }

    @Test
    void blankQueryMeansNoTextSearch() {
        assertThat(searchService.search("   ", null)).hasSize(3);
    }

    @Test
    void noMatch_returnsEmptyList() {
        assertThat(searchService.search("findes ikke", null)).isEmpty();
    }

    @Test
    void resultContainsYearEventSpeakersAndAvailability() {
        VideoSearchResult result = searchService.search("sundhed", null).getFirst();

        assertThat(result.getYear()).isEqualTo(2025);
        assertThat(result.getEventTitle()).isEqualTo("Folkemødet 2025");
        assertThat(result.getSpeakerNames()).containsExactly("Ali Khan", "Mette Hansen");
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getPrice()).isEqualByComparingTo("49.00");
    }

    @Test
    void getAvailableYears_returnsYearsNewestFirst() {
        when(eventRepository.findAllByOrderByYearDesc()).thenReturn(List.of(event2026, event2025));

        assertThat(searchService.getAvailableYears()).containsExactly(2026, 2025);
    }
}
