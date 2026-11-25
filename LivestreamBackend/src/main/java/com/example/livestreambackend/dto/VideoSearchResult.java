package com.example.livestreambackend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * One video in the search results, with only what the page needs.
 * The page gets this instead of the Video entity, so it never touches lazy JPA fields.
 */
public class VideoSearchResult {

    private final Long id;
    private final String title;
    private final int year;
    private final String eventTitle;
    private final List<String> speakerNames;
    private final BigDecimal price;
    private final boolean available;

    public VideoSearchResult(Long id, String title, int year, String eventTitle,
                             List<String> speakerNames, BigDecimal price, boolean available) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.eventTitle = eventTitle;
        this.speakerNames = speakerNames;
        this.price = price;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public List<String> getSpeakerNames() {
        return speakerNames;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }
}
