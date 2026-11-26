package com.example.livestreambackend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Én video i kataloget på forsiden, med det en gæst må se:
 * titel, talere, beskrivelse, varighed, pris og thumbnail (ikke selve videoen).
 * Siden får denne klasse i stedet for Video-entiteten, så den aldrig rører lazy JPA-felter.
 */
public class CatalogVideo {

    private final Long id;
    private final String title;
    private final String description;
    private final List<String> speakerNames;
    private final Integer durationMinutes;
    private final BigDecimal price;
    private final String thumbnailUrl;
    private final boolean available;

    public CatalogVideo(Long id, String title, String description, List<String> speakerNames,
                        Integer durationMinutes, BigDecimal price, String thumbnailUrl, boolean available) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.speakerNames = speakerNames;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSpeakerNames() {
        return speakerNames;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /** False, hvis videoen er oprettet, men endnu ikke uploadet til videotjenesten. */
    public boolean isAvailable() {
        return available;
    }
}
