package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
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
            log.info(" StatClient.getStats() вызван");

            String startStr = start.format(DATE_TIME_FORMATTER);
            String endStr = end.format(DATE_TIME_FORMATTER);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(statsServerUrl + "/stats")
                    .queryParam("start", startStr)
                    .queryParam("end", endStr)
                    .queryParam("unique", unique);

            if (uris != null && !uris.isEmpty()) {
                for (String uri : uris) {
                    builder.queryParam("uris", uri);
                }
            }

            String url = builder.build().toUriString();
            log.info(" STATCLIENT URL: {}", url);

            ResponseEntity<List<ViewStats>> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    VIEW_STATS_LIST_TYPE
            );



            return response.getBody();

        } catch (Exception e) {
            log.error(" Ошибка в StatClient: {}", e.getMessage(), e);
            throw new StatsClientException("Error getting stats", e);
        }
    }
}
