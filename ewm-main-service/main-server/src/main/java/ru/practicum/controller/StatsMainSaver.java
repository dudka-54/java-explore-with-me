package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatClient;

import java.time.LocalDateTime;


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
}
