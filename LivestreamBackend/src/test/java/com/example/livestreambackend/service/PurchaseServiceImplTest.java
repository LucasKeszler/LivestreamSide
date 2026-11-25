package com.example.livestreambackend.service;

import com.example.livestreambackend.exception.AlreadyPurchasedException;
import com.example.livestreambackend.exception.NotFoundException;
import com.example.livestreambackend.model.*;
import com.example.livestreambackend.repository.AccountRepository;
import com.example.livestreambackend.repository.EventRepository;
import com.example.livestreambackend.repository.PurchaseRepository;
import com.example.livestreambackend.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseServiceImplTest {

    private PurchaseRepository purchaseRepository;
    private AccountRepository accountRepository;
    private VideoRepository videoRepository;
    private EventRepository eventRepository;
    private PurchaseService purchaseService;

    private Account account;
    private Event event;
    private Video video;

    @BeforeEach
    void setUp() {
        purchaseRepository = mock(PurchaseRepository.class);
        accountRepository = mock(AccountRepository.class);
        videoRepository = mock(VideoRepository.class);
        eventRepository = mock(EventRepository.class);
        purchaseService = new PurchaseServiceImpl(purchaseRepository, accountRepository, videoRepository, eventRepository);

        account = new Account("mads@example.com", "Mads", "hashed", Role.USER);
        event = new Event("Folkemødet 2026", new BigDecimal("299.00"), "Allinge", 2026);
        video = new Video("Klimadebat", "Debat om klima", 60, new BigDecimal("49.00"), null, event, "vid-123");

        when(accountRepository.findByEmail("mads@example.com")).thenReturn(Optional.of(account));
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(purchaseRepository.saveAndFlush(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---------- buyVideo ----------

    @Test
    void buyVideo_savesVideoPurchaseWithCurrentPrice() {
        Purchase purchase = purchaseService.buyVideo("mads@example.com", 1L);

        assertThat(purchase.getType()).isEqualTo(PurchaseType.VIDEO);
        assertThat(purchase.getAccount()).isSameAs(account);
        assertThat(purchase.getVideo()).isSameAs(video);
        assertThat(purchase.getEvent()).isNull();
        assertThat(purchase.getPricePaid()).isEqualByComparingTo("49.00");
        assertThat(purchase.getPurchasedAt()).isNotNull();
    }

    @Test
    void buyVideo_normalizesEmailBeforeLookup() {
        purchaseService.buyVideo("  Mads@Example.COM ", 1L);

        verify(accountRepository).findByEmail("mads@example.com");
    }

    @Test
    void buyVideo_rejectsVideoAlreadyBought() {
        when(purchaseRepository.existsByAccountAndVideo(account, video)).thenReturn(true);

        assertThatThrownBy(() -> purchaseService.buyVideo("mads@example.com", 1L))
                .isInstanceOf(AlreadyPurchasedException.class);
        verify(purchaseRepository, never()).saveAndFlush(any());
    }

    @Test
    void buyVideo_rejectsVideoCoveredByYearPass() {
        when(purchaseRepository.existsByAccountAndEvent(account, event)).thenReturn(true);

        assertThatThrownBy(() -> purchaseService.buyVideo("mads@example.com", 1L))
                .isInstanceOf(AlreadyPurchasedException.class);
        verify(purchaseRepository, never()).saveAndFlush(any());
    }

    @Test
    void buyVideo_unknownVideo_throwsNotFound() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.buyVideo("mads@example.com", 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void buyVideo_unknownAccount_throwsNotFound() {
        assertThatThrownBy(() -> purchaseService.buyVideo("ghost@example.com", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void buyVideo_translatesUniqueConstraintViolation() {
        when(purchaseRepository.saveAndFlush(any(Purchase.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> purchaseService.buyVideo("mads@example.com", 1L))
                .isInstanceOf(AlreadyPurchasedException.class);
    }

    // ---------- buyYearPass ----------

    @Test
    void buyYearPass_savesYearPassWithEventPrice() {
        Purchase purchase = purchaseService.buyYearPass("mads@example.com", 10L);

        assertThat(purchase.getType()).isEqualTo(PurchaseType.YEAR_PASS);
        assertThat(purchase.getEvent()).isSameAs(event);
        assertThat(purchase.getVideo()).isNull();
        assertThat(purchase.getPricePaid()).isEqualByComparingTo("299.00");
    }

    @Test
    void buyYearPass_rejectsSecondYearPassForSameEvent() {
        when(purchaseRepository.existsByAccountAndEvent(account, event)).thenReturn(true);

        assertThatThrownBy(() -> purchaseService.buyYearPass("mads@example.com", 10L))
                .isInstanceOf(AlreadyPurchasedException.class);
        verify(purchaseRepository, never()).saveAndFlush(any());
    }

    @Test
    void buyYearPass_unknownEvent_throwsNotFound() {
        when(eventRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.buyYearPass("mads@example.com", 77L))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- hasAccessToVideo ----------

    @Test
    void hasAccess_falseWhenNothingBought() {
        assertThat(purchaseService.hasAccessToVideo("mads@example.com", 1L)).isFalse();
    }

    @Test
    void hasAccess_trueWhenVideoBought() {
        when(purchaseRepository.existsByAccountAndVideo(account, video)).thenReturn(true);

        assertThat(purchaseService.hasAccessToVideo("mads@example.com", 1L)).isTrue();
    }

    @Test
    void hasAccess_trueWhenYearPassForVideosEvent() {
        when(purchaseRepository.existsByAccountAndEvent(account, event)).thenReturn(true);

        assertThat(purchaseService.hasAccessToVideo("mads@example.com", 1L)).isTrue();
    }

    // ---------- getPurchases ----------

    @Test
    void getPurchases_returnsRepositoryResult() {
        Purchase p = Purchase.ofVideo(account, video);
        when(purchaseRepository.findByAccountOrderByPurchasedAtDesc(account)).thenReturn(List.of(p));

        assertThat(purchaseService.getPurchases("mads@example.com")).containsExactly(p);
    }
}
