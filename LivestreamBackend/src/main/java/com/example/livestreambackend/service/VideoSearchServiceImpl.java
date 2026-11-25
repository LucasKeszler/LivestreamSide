package com.example.livestreambackend.service;

import com.example.livestreambackend.dto.VideoSearchResult;
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

@Service
public class VideoSearchServiceImpl implements VideoSearchService {

    private final VideoRepository videoRepository;
    private final EventRepository eventRepository;

    public VideoSearchServiceImpl(VideoRepository videoRepository, EventRepository eventRepository) {
        this.videoRepository = videoRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoSearchResult> search(String query, Integer year) {
        String text = normalize(query);

        return videoRepository.findAllBy().stream()
                .filter(video -> year == null || video.getEvent().getYear() == year)
                .filter(video -> text.isEmpty() || matchesText(video, text))
                .sorted(Comparator
                        .comparing((Video video) -> video.getEvent().getYear()).reversed()
                        .thenComparing(video -> video.getTitle().toLowerCase(Locale.ROOT)))
                .map(this::toResult)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAvailableYears() {
        return eventRepository.findAllByOrderByYearDesc().stream()
                .map(Event::getYear)
                .toList();
    }

    /** True if the title or any speaker's name contains the text. */
    private boolean matchesText(Video video, String text) {
        if (contains(video.getTitle(), text)) {
            return true;
        }
        return video.getSpeakers().stream()
                .anyMatch(speaker -> contains(speaker.getName(), text));
    }

    private boolean contains(String value, String text) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(text);
    }

    /** Trims and lower-cases the search text; null becomes "". */
    private String normalize(String query) {
        return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    }

    private VideoSearchResult toResult(Video video) {
        List<String> speakerNames = video.getSpeakers().stream()
                .map(Speaker::getName)
                .sorted()
                .toList();

        return new VideoSearchResult(
                video.getId(),
                video.getTitle(),
                video.getEvent().getYear(),
                video.getEvent().getTitle(),
                speakerNames,
                video.getPrice(),
                video.isAvailable());
    }
}
