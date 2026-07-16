package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "locations")
@Embeddable
public class Location {
    @Column(nullable = false)
    private Float lat;

    @Column(nullable = false)
    private Float lon;
}
