package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatClient;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;


public interface StatsMainSaver {
    String APP_NAME = "ewm-main-service";

    StatClient getStatsClient();

    default void saveStat(String uri, HttpServletRequest request) {
        StatClient statsClient = getStatsClient();
        if (statsClient != null) {
            statsClient.saveHit(EndpointHitDto.builder()
                    .app(APP_NAME)
                    .uri(uri)
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    default void saveStat(String uri, HttpServletRequest request, String additionalInfo) {
        StatClient statsClient = getStatsClient();
        if (statsClient != null) {
            statsClient.saveHit(EndpointHitDto.builder()
                    .app(APP_NAME)
                    .uri(uri + (additionalInfo != null ? "/" + additionalInfo : ""))
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    default Long getEventViews(Long eventId) {
        StatClient statsClient = getStatsClient();
        if (statsClient == null) {
            return 0L;
        }

        try {
            List<ViewStats> stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(100),
                    LocalDateTime.now(),
                    List.of("/events/" + eventId),
                    false
            );
            return stats.isEmpty() ? 0L : stats.get(0).getHits();
        } catch (Exception e) {
            return 0L;
        }
    }
}
