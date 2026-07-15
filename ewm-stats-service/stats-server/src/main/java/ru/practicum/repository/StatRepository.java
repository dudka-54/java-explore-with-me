package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    @Query("""
            SELECT new ru.practicum.ViewStats(e.app, e.uri,
            CASE WHEN :unique = true THEN COUNT(DISTINCT e.ip) ELSE COUNT(e) END)
            FROM EndpointHit AS e
            WHERE e.timestamp BETWEEN :start AND :end
                AND (:uris IS NULL OR e.uri IN :uris)
            GROUP BY e.app, e.uri
            ORDER BY
                CASE WHEN :unique = true THEN COUNT(DISTINCT e.ip) ELSE COUNT(e) END DESC
            """)
    List<ViewStats> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            @Param("unique") boolean unique
    );
}
