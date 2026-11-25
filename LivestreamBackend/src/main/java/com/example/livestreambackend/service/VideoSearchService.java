package com.example.livestreambackend.service;

import com.example.livestreambackend.dto.VideoSearchResult;

import java.util.List;

/**
 * Searching and filtering the video catalogue (user story #5).
 */
public interface VideoSearchService {

    /**
     * Finds videos whose title or one of whose speakers contains the search text,
     * optionally only from one year. Matching ignores upper/lower case.
     *
     * @param query search text; null or blank means "no text search"
     * @param year  only videos from this year; null means "all years"
     * @return matching videos, newest year first and then by title; empty list if nothing matches
     */
    List<VideoSearchResult> search(String query, Integer year);

    /**
     * The years that have an event, newest first. Used for the year filter.
     */
    List<Integer> getAvailableYears();
}
