package ru.practicum.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.id IN :ids AND r.status = 'PENDING'")
    List<Request> findPendingRequestsByIds(@Param("ids") List<Long> ids);

    @Query("SELECT r FROM Request r WHERE r.event.id = :eventId " +
            "AND r.status = 'PENDING' " +
            "AND r.id NOT IN :excludedIds")
    List<Request> findPendingRequestsByEventIdExcludingIds(
            @Param("eventId") Long eventId,
            @Param("excludedIds") List<Long> excludedIds
    );

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}
