package ru.practicum.service;

import jakarta.validation.Valid;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    void save(@Valid EndpointHitDto hit);
}
