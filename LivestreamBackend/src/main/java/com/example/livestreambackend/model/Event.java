package com.example.livestreambackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(name = "event_year", nullable = false, unique = true)
    private int year;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal yearPassPrice;

    protected Event() {
    }

    public Event(String title, BigDecimal yearPassPrice, String location, int year) {
        this.title = title;
        this.yearPassPrice = yearPassPrice;
        this.location = location;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getYearPassPrice() {
        return yearPassPrice;
    }

    public String getLocation() {
        return location;
    }

    public int getYear() {
        return year;
    }
}
