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
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "event_id")
    private Event event;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(nullable = false, name = "requester_id")
    private User requester;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
