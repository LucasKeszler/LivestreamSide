package com.example.livestreambackend.repository;

import com.example.livestreambackend.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {
}
