package com.example.livestreambackend.service;

import com.example.livestreambackend.dto.CatalogVideo;
import com.example.livestreambackend.dto.YearVideoGroup;
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

class VideoCatalogServiceImplTest {

    private VideoRepository videoRepository;
    private EventRepository eventRepository;
    private VideoCatalogService catalogService;

    private Event event2024;
    private Event event2025;
    private Event event2026;

    @BeforeEach
    void setUp() {
        videoRepository = mock(VideoRepository.class);
        eventRepository = mock(EventRepository.class);
        catalogService = new VideoCatalogServiceImpl(videoRepository, eventRepository);

        event2024 = new Event("Folkemødet 2024", new BigDecimal("199.00"), "Allinge", 2024);
        event2025 = new Event("Folkemødet 2025", new BigDecimal("249.00"), "Allinge", 2025);
        event2026 = new Event("Folkemødet 2026", new BigDecimal("299.00"), "Allinge", 2026);

        when(eventRepository.findAllByOrderByYearDesc()).thenReturn(List.of(event2026, event2025, event2024));
    }

    private Video video(String title, Event event, String reference, Speaker... speakers) {
        Video video = new Video(title, "Beskrivelse", 45, new BigDecimal("49.00"), "thumb.jpg", event, reference);
        for (Speaker speaker : speakers) {
            video.addSpeaker(speaker);
        }
        return video;
    }

    private List<String> titles(YearVideoGroup group) {
        return group.getVideos().stream().map(CatalogVideo::getTitle).toList();
    }

    @Test
    void groupsVideosByYear_newestYearFirst() {
        when(videoRepository.findAllBy()).thenReturn(List.of(
                video("Sundhed for alle", event2025, "vid-1"),
                video("Klimadebat", event2026, "vid-2"),
                video("Fremtidens skole", event2026, "vid-3")));

        List<YearVideoGroup> groups = catalogService.getVideosGroupedByYear();

        assertThat(groups).extracting(YearVideoGroup::getYear).containsExactly(2026, 2025, 2024);
        assertThat(titles(groups.get(0))).containsExactlyInAnyOrder("Klimadebat", "Fremtidens skole");
        assertThat(titles(groups.get(1))).containsExactly("Sundhed for alle");
    }

    @Test
    void videosWithinAYear_areSortedByTitle_ignoringCase() {
        when(videoRepository.findAllBy()).thenReturn(List.of(
                video("klimadebat", event2026, "vid-1"),
                video("Bæredygtighed", event2026, "vid-2"),
                video("Fremtidens skole", event2026, "vid-3")));

        YearVideoGroup group2026 = catalogService.getVideosGroupedByYear().getFirst();

        assertThat(titles(group2026)).containsExactly("Bæredygtighed", "Fremtidens skole", "klimadebat");
    }

    @Test
    void eventWithoutVideos_isIncludedWithEmptyList() {
        when(videoRepository.findAllBy()).thenReturn(List.of(video("Klimadebat", event2026, "vid-1")));

        YearVideoGroup group2024 = catalogService.getVideosGroupedByYear().get(2);

        assertThat(group2024.getYear()).isEqualTo(2024);
        assertThat(group2024.getVideos()).isEmpty();
    }

    @Test
    void noEvents_returnsEmptyList() {
        when(eventRepository.findAllByOrderByYearDesc()).thenReturn(List.of());
        when(videoRepository.findAllBy()).thenReturn(List.of());

        assertThat(catalogService.getVideosGroupedByYear()).isEmpty();
    }

    @Test
    void groupContainsEventInfoAndYearPassPrice() {
        when(videoRepository.findAllBy()).thenReturn(List.of());

        YearVideoGroup group2026 = catalogService.getVideosGroupedByYear().getFirst();

        assertThat(group2026.getEventTitle()).isEqualTo("Folkemødet 2026");
        assertThat(group2026.getLocation()).isEqualTo("Allinge");
        assertThat(group2026.getYearPassPrice()).isEqualByComparingTo("299.00");
    }

    @Test
    void videoContainsCatalogInfo_speakersSortedByName() {
        when(videoRepository.findAllBy()).thenReturn(List.of(
                video("Sundhed for alle", event2025, null, new Speaker("Mette Hansen"), new Speaker("Ali Khan"))));

        CatalogVideo video = catalogService.getVideosGroupedByYear().get(1).getVideos().getFirst();

        assertThat(video.getTitle()).isEqualTo("Sundhed for alle");
        assertThat(video.getDescription()).isEqualTo("Beskrivelse");
        assertThat(video.getSpeakerNames()).containsExactly("Ali Khan", "Mette Hansen");
        assertThat(video.getDurationMinutes()).isEqualTo(45);
        assertThat(video.getPrice()).isEqualByComparingTo("49.00");
        assertThat(video.getThumbnailUrl()).isEqualTo("thumb.jpg");
        assertThat(video.isAvailable()).isFalse();
    }
}
