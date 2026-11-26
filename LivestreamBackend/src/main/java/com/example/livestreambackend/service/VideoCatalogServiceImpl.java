package com.example.livestreambackend.service;

import com.example.livestreambackend.dto.CatalogVideo;
import com.example.livestreambackend.dto.YearVideoGroup;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.model.Speaker;
import com.example.livestreambackend.model.Video;
import com.example.livestreambackend.repository.EventRepository;
import com.example.livestreambackend.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class VideoCatalogServiceImpl implements VideoCatalogService {

    private final VideoRepository videoRepository;
    private final EventRepository eventRepository;

    public VideoCatalogServiceImpl(VideoRepository videoRepository, EventRepository eventRepository) {
        this.videoRepository = videoRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<YearVideoGroup> getVideosGroupedByYear() {
        // To forespørgsler i alt: alle events og alle videoer (med talere hentet med det samme).
        // Videoerne grupperes på året, som er unikt pr. event.
        Map<Integer, List<Video>> videosByYear = videoRepository.findAllBy().stream()
                .collect(groupingBy(video -> video.getEvent().getYear()));

        return eventRepository.findAllByOrderByYearDesc().stream()
                .map(event -> toGroup(event, videosByYear.getOrDefault(event.getYear(), List.of())))
                .toList();
    }

    private YearVideoGroup toGroup(Event event, List<Video> videos) {
        List<CatalogVideo> catalogVideos = videos.stream()
                .sorted(Comparator.comparing(video -> video.getTitle().toLowerCase(Locale.ROOT)))
                .map(this::toCatalogVideo)
                .toList();

        return new YearVideoGroup(
                event.getYear(),
                event.getTitle(),
                event.getLocation(),
                event.getYearPassPrice(),
                catalogVideos);
    }

    private CatalogVideo toCatalogVideo(Video video) {
        List<String> speakerNames = video.getSpeakers().stream()
                .map(Speaker::getName)
                .sorted()
                .toList();

        return new CatalogVideo(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                speakerNames,
                video.getDurationMinutes(),
                video.getPrice(),
                video.getThumbnailUrl(),
                video.isAvailable());
    }
}
