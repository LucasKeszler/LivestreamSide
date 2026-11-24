package com.example.livestreambackend.model;

import jakarta.persistence.*;


@Entity
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title;

    private String organization;

    @Column(length = 1000)
    private String bio;

    protected Speaker() {
    }

    public Speaker(String name) {
        this(name, null, null, null);
    }

    public Speaker(String name, String title, String organization, String bio) {
        this.name = name;
        this.organization = organization;
        this.title = title;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getOrganization() {
        return organization;
    }

    public String getTitle() {
        return title;
    }
}
