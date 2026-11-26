package com.example.livestreambackend.repository;

import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.model.Purchase;
import com.example.livestreambackend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /** Has the account bought this single video? */
    boolean existsByAccountAndVideo(Account account, Video video);

    /** Has the account bought a year pass for this event? */
    boolean existsByAccountAndEvent(Account account, Event event);

    /** All purchases for one account, newest first (for a "My purchases" page). */
    List<Purchase> findByAccountOrderByPurchasedAtDesc(Account account);
}
