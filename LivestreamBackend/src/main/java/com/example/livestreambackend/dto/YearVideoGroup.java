package com.example.livestreambackend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ét år (ét event) i kataloget med dets videoer.
 * Bruges til forsiden, hvor videoerne vises grupperet pr. år.
 */
public class YearVideoGroup {

    private final int year;
    private final String eventTitle;
    private final String location;
    private final BigDecimal yearPassPrice;
    private final List<CatalogVideo> videos;

    public YearVideoGroup(int year, String eventTitle, String location,
                          BigDecimal yearPassPrice, List<CatalogVideo> videos) {
        this.year = year;
        this.eventTitle = eventTitle;
        this.location = location;
        this.yearPassPrice = yearPassPrice;
        this.videos = videos;
    }

    public int getYear() {
        return year;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getLocation() {
        return location;
    }

    public BigDecimal getYearPassPrice() {
        return yearPassPrice;
    }

    public List<CatalogVideo> getVideos() {
        return videos;
    }
}
