package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class StatClient {
    protected final RestTemplate rest;

    @Value("${stats-server.url:http://localhost:9090}")
    private String statsServerUrl;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ParameterizedTypeReference<List<ViewStats>> VIEW_STATS_LIST_TYPE =
            new ParameterizedTypeReference<>() {
            };


    public void saveHit(EndpointHitDto hit) {
        try {
            log.debug("Saving hit: app={}, uri={}, ip={}",
                    hit.getApp(), hit.getUri(), hit.getIp());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hit, headers);

            ResponseEntity<Void> response = rest.postForEntity(
                    statsServerUrl + "/hit",
                    requestEntity,
                    Void.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to save hit. Status: {}", response.getStatusCode());
                throw new StatsClientException("Failed to save hit: " + response.getStatusCode());
            }

            log.info("Hit saved: app={}, uri={}", hit.getApp(), hit.getUri());

        } catch (RestClientException e) {
            log.error("Error saving hit: {}", e.getMessage(), e);
            throw new StatsClientException("Error saving hit", e);
        }
    }

    public List<ViewStats> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
    ) {
        try {
            String encodedStart = URLEncoder.encode(
                    start.format(DATE_TIME_FORMATTER),
                    StandardCharsets.UTF_8
            );
            String encodedEnd = URLEncoder.encode(
                    end.format(DATE_TIME_FORMATTER),
                    StandardCharsets.UTF_8
            );

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(statsServerUrl + "/stats")
                    .queryParam("start", encodedStart)
                    .queryParam("end", encodedEnd)
                    .queryParam("unique", unique);

            if (uris != null && !uris.isEmpty()) {
                builder.queryParam("uris", uris);
            }

            String url = builder.build().toUriString();
            log.debug("Requesting stats: {}", url);

            ResponseEntity<List<ViewStats>> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    VIEW_STATS_LIST_TYPE
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to get stats. Status: {}", response.getStatusCode());
                throw new StatsClientException("Failed to get stats: " + response.getStatusCode());
            }

            log.info("Stats received: {} records", response.getBody().size());
            return response.getBody();

        } catch (RestClientException e) {
            log.error("Error getting stats: {}", e.getMessage(), e);
            throw new StatsClientException("Error getting stats", e);
        }
    }
}
