package com.example.livestreambackend.repository;

import com.example.livestreambackend.model.Video;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByEventIdOrderByTitleAsc(Long eventId);

    /**
     * All videos with their event and speakers loaded in the same query.
     * Without the EntityGraph, the speakers would be loaded lazily, one extra query per video,
     * and reading them outside a transaction would fail (open-in-view is off).
     */
    @EntityGraph(attributePaths = {"event", "speakers"})
    List<Video> findAllBy();
}
