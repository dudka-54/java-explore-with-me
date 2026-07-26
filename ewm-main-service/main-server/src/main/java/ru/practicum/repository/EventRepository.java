package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.model.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.initiator.id = :userId")
    Page<Event> findEventsByInitiatorId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT DISTINCT e FROM Event e
            WHERE (:users IS NULL OR e.initiator.id IN :users)
                AND (:statuses IS NULL OR e.state IN :statuses)
                AND (:categories IS NULL OR e.category.id IN :categories)
                AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart)
                AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)
            ORDER BY e.eventDate DESC
            """)
    Page<Event> findAdminEvents(
            @Param("users") List<Long> users,
            @Param("statuses") List<EventStatus> statuses,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT e FROM Event e
            WHERE e.state = 'PUBLISHED'
                AND (COALESCE(:text, '') = ''
                    OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
                AND (:categories IS NULL OR e.category.id IN :categories)
                AND (:paid IS NULL OR e.paid = :paid)
                AND e.eventDate BETWEEN :rangeStart AND :rangeEnd
                AND (:onlyAvailable = false
                    OR e.participantLimit = 0
                    OR e.confirmedRequests < e.participantLimit)
            """)
    Page<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable,
            Pageable pageable
    );

    boolean existsByCategoryId(Long categoryId);

}
