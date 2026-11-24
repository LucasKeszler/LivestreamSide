package com.example.livestreambackend.repository;

import com.example.livestreambackend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByYearDesc();

    boolean existsByYear(int year);
}
