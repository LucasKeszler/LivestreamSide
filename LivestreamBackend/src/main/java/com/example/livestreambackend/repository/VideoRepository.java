package com.example.livestreambackend.repository;

import com.example.livestreambackend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByEventIdOrderByTitleAsc(Long eventId);
}
