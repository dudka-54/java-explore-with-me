package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "endpoints")
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String app;

    @Column(nullable = false, length = 512)
    private String uri;

    @Column(nullable = false, length = 45)
    private String ip;

    @Column(nullable = false, name = "times", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime timestamp;
}
