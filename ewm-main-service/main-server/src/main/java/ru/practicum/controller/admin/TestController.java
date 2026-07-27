package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ViewStats;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @GetMapping("/force-views/{id}")
    public ResponseEntity<Long> forceViews(@PathVariable Long id) {
        try {
            log.info("🧪 Тестовый запрос для eventId={}", id);

            // Прямой запрос к stats-server через RestTemplate
            RestTemplate rest = new RestTemplate();
            String url = "http://stats-server:9090/stats?start=2026-07-27%2000:00:00&end=2026-07-28%2000:00:00&uris=/events/" + id + "&unique=false";

            log.info("🧪 URL: {}", url);

            ResponseEntity<ViewStats[]> response = rest.getForEntity(url, ViewStats[].class);
            ViewStats[] stats = response.getBody();

            Long hits = (stats != null && stats.length > 0) ? stats[0].getHits() : 0L;

            log.info("🧪 Результат: hits={}", hits);
            return ResponseEntity.ok(hits);

        } catch (Exception e) {
            log.error("🧪 Ошибка: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}