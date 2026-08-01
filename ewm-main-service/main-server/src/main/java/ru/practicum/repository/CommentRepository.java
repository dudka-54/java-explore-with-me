package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;
import ru.practicum.model.CommentStatus;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByEventIdAndStatus(Pageable pageable, Long eventId, CommentStatus status);

    Optional<Comment> findByCommentatorIdAndEventId(Long commentatorId, Long eventId);

    Page<Comment> findByStatus(Pageable pageable, CommentStatus status);
}
