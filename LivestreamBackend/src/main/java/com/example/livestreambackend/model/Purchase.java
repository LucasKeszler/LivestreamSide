package com.example.livestreambackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A purchase made by an account: either one video or a year pass for one event.
 *
 * Exactly one of {@code video} and {@code event} is set, depending on {@code type}.
 * Use the factory methods {@link #ofVideo} and {@link #ofYearPass} so that rule always holds.
 *
 * The price is copied at the time of purchase, so later price changes on the video
 * or event do not change what the customer actually paid.
 */
@Entity
@Table(uniqueConstraints = {
        // The database stops the same account from buying the same thing twice,
        // even if two requests arrive at the same time.
        @UniqueConstraint(name = "uk_purchase_account_video", columnNames = {"account_id", "video_id"}),
        @UniqueConstraint(name = "uk_purchase_account_event", columnNames = {"account_id", "event_id"})
})
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseType type;

    /** Set when type is VIDEO, otherwise null. */
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    /** Set when type is YEAR_PASS, otherwise null. */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    protected Purchase() {
    }

    private Purchase(Account account, PurchaseType type, Video video, Event event, BigDecimal pricePaid) {
        this.account = account;
        this.type = type;
        this.video = video;
        this.event = event;
        this.pricePaid = pricePaid;
        this.purchasedAt = LocalDateTime.now();
    }

    public static Purchase ofVideo(Account account, Video video) {
        return new Purchase(account, PurchaseType.VIDEO, video, null, video.getPrice());
    }

    public static Purchase ofYearPass(Account account, Event event) {
        return new Purchase(account, PurchaseType.YEAR_PASS, null, event, event.getYearPassPrice());
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public PurchaseType getType() {
        return type;
    }

    public Video getVideo() {
        return video;
    }

    public Event getEvent() {
        return event;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }
}
