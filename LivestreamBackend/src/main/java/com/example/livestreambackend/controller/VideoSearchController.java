package com.example.livestreambackend.controller;

import com.example.livestreambackend.dto.VideoSearchResult;
import com.example.livestreambackend.service.VideoSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Search page for guests: search in title and speaker, filter on year (user story #5).
 * Uses GET with query parameters, e.g. /search?q=klima&year=2026,
 * so a search can be bookmarked or shared as a link.
 */
@Controller
public class VideoSearchController {

    private final VideoSearchService videoSearchService;

    public VideoSearchController(VideoSearchService videoSearchService) {
        this.videoSearchService = videoSearchService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String query,
                         @RequestParam(required = false) Integer year,
                         Model model) {
        List<VideoSearchResult> results = videoSearchService.search(query, year);

        model.addAttribute("results", results);
        model.addAttribute("years", videoSearchService.getAvailableYears());
        // Sent back so the form keeps what the user typed and chose.
        model.addAttribute("q", query);
        model.addAttribute("year", year);
        return "search";
    }
}
