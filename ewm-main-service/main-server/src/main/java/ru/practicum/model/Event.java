package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Column(nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private Category category;

    @Column(nullable = false, name = "confirmed_requests")
    private Integer confirmedRequests;

    @Column(nullable = false, unique = true)
    private String description;

    @Column(nullable = false, name = "event_date")
    private LocalDateTime eventDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "initiator_id")
    private User initiator;

    @Embedded
    private Location location;

    @Column(nullable = false)
    @Builder.Default
    private Boolean paid = true;

    @Column(name = "participant_limit", nullable = false)
    @Builder.Default
    private Integer participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation", nullable = false)
    @Builder.Default
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus state;

    @Column(nullable = false, length = 512)
    private String title;

    @Transient
    private Integer views;
}
