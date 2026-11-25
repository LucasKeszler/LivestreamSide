package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.AlreadyPurchasedException;
import com.example.livestreambackend.exception.NotFoundException;
import com.example.livestreambackend.model.Account;
import com.example.livestreambackend.model.Event;
import com.example.livestreambackend.model.Purchase;
import com.example.livestreambackend.model.Video;
import com.example.livestreambackend.repository.AccountRepository;
import com.example.livestreambackend.repository.EventRepository;
import com.example.livestreambackend.repository.PurchaseRepository;
import com.example.livestreambackend.repository.VideoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final AccountRepository accountRepository;
    private final VideoRepository videoRepository;
    private final EventRepository eventRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
                               AccountRepository accountRepository,
                               VideoRepository videoRepository,
                               EventRepository eventRepository) {
        this.purchaseRepository = purchaseRepository;
        this.accountRepository = accountRepository;
        this.videoRepository = videoRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public Purchase buyVideo(String email, Long videoId) {
        Account account = findAccount(email);
        Video video = findVideo(videoId);

        // Buying a video you can already watch would just be paying twice.
        if (hasAccess(account, video)) {
            throw new AlreadyPurchasedException("You already have access to this video");
        }

        return save(Purchase.ofVideo(account, video));
    }

    @Override
    @Transactional
    public Purchase buyYearPass(String email, Long eventId) {
        Account account = findAccount(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        if (purchaseRepository.existsByAccountAndEvent(account, event)) {
            throw new AlreadyPurchasedException("You already have a year pass for this event");
        }

        return save(Purchase.ofYearPass(account, event));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessToVideo(String email, Long videoId) {
        return hasAccess(findAccount(email), findVideo(videoId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> getPurchases(String email) {
        return purchaseRepository.findByAccountOrderByPurchasedAtDesc(findAccount(email));
    }

    private boolean hasAccess(Account account, Video video) {
        return purchaseRepository.existsByAccountAndVideo(account, video)
                || purchaseRepository.existsByAccountAndEvent(account, video.getEvent());
    }

    private Purchase save(Purchase purchase) {
        try {
            return purchaseRepository.saveAndFlush(purchase);
        } catch (DataIntegrityViolationException e) {
            // Two clicks on "Buy" at the same time: the unique constraint in the database catches the second one.
            throw new AlreadyPurchasedException("This has already been purchased");
        }
    }

    private Account findAccount(String email) {
        return accountRepository.findByEmail(Account.normalizeEmail(email))
                .orElseThrow(() -> new NotFoundException("Account not found: " + email));
    }

    private Video findVideo(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + videoId));
    }
}
