package com.example.livestreambackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private Integer durationMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(unique = true)
    private String videoReference;

    private String thumbnailUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToMany
    @JoinTable(
            name = "video_speaker",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "speaker_id")
    )
    private Set<Speaker> speakers = new HashSet<>();

    protected Video() {}

    public Video(String title, String description, Integer durationMinutes, BigDecimal price, String thumbnailUrl, Event event, String videoReference) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.event = event;
        this.videoReference = videoReference;
    }

    public void attachVideo(String videoReference) {
        this.videoReference = videoReference;
    }

    public boolean isAvailable() {
        return videoReference != null;
    }

    public void addSpeaker(Speaker speaker) {
        speakers.add(speaker);
    }

    public void removeSpeaker(Speaker speaker) {
        speakers.remove(speaker);
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Event getEvent() {
        return event;
    }

    public Set<Speaker> getSpeakers() {
        return Collections.unmodifiableSet(speakers);
    }

    public String getVideoReference() {
        return videoReference;
    }
}