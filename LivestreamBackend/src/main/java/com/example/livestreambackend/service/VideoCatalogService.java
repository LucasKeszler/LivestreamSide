package com.example.livestreambackend.service;

import com.example.livestreambackend.dto.YearVideoGroup;

import java.util.List;

/**
 * Videokataloget på forsiden: alle videoer grupperet pr. år (ét event pr. år).
 */
public interface VideoCatalogService {

    /**
     * Alle events med deres videoer, nyeste år først.
     * Inden for hvert år er videoerne sorteret efter titel.
     * Et event uden videoer er med i listen med en tom videoliste,
     * så et nyoprettet år kan vises, før videoerne er lagt op.
     *
     * @return én gruppe pr. event; tom liste, hvis der ingen events er
     */
    List<YearVideoGroup> getVideosGroupedByYear();
}
