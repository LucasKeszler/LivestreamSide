package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.AlreadyPurchasedException;
import com.example.livestreambackend.exception.NotFoundException;
import com.example.livestreambackend.model.Purchase;

import java.util.List;

/**
 * The contract for buying videos and year passes, and for checking who may watch what.
 *
 * Accounts are identified by email, because that is the username Spring Security
 * gives us for the logged-in user (see AccountUserDetailsService).
 * No real payment happens here yet; a purchase is simply registered.
 */
public interface PurchaseService {

    /**
     * Buys a single video.
     *
     * @throws NotFoundException        if the account or video does not exist
     * @throws AlreadyPurchasedException if the account already owns the video
     *                                   or has a year pass that covers it
     */
    Purchase buyVideo(String email, Long videoId);

    /**
     * Buys a year pass, which gives access to every video in the event.
     *
     * @throws NotFoundException        if the account or event does not exist
     * @throws AlreadyPurchasedException if the account already has a year pass for the event
     */
    Purchase buyYearPass(String email, Long eventId);

    /**
     * True if the account may watch the video: it bought the video itself,
     * or it has a year pass for the video's event.
     *
     * @throws NotFoundException if the account or video does not exist
     */
    boolean hasAccessToVideo(String email, Long videoId);

    /**
     * All purchases for the account, newest first.
     *
     * @throws NotFoundException if the account does not exist
     */
    List<Purchase> getPurchases(String email);
}
