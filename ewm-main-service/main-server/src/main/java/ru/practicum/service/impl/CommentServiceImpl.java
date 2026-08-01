package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.PatchCommentDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(Long eventId, Long userId, NewCommentDto dto) {
        log.info("Создание комментария по: userId={}, eventId={}", userId, eventId);

        User user = findUserOrThrow(userId);

        Event event = findEventOrThrow(eventId);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Нельзя оставлять комментарии к неопубликованному событию");
        }
        Comment comment = commentMapper.toEntity(dto);

        comment.setCommentator(user);
        comment.setCreated(LocalDateTime.now());
        comment.setEvent(event);
        comment.setStatus(CommentStatus.PENDING);

        Comment newComment = commentRepository.save(comment);

        return commentMapper.toDto(newComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long eventId, Long commentId, Long userId, PatchCommentDto dto) {
        log.info("Обновление комментария commentId={} по: userId={}, eventId={}", commentId, userId, eventId);

        User user = findUserOrThrow(userId);

        Event event = findEventOrThrow(eventId);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Нельзя обновлять комментарии к неопубликованному событию");
        }

        Comment comment = findCommentOrThrow(commentId);

        if (!(commentBelongsToEvent(comment, eventId))) {
            throw new ConflictException("Комментарий не принадлежит указанному событию");
        }

        if (!(userIsOwner(comment, userId))) {
            throw new ConflictException("Только автор может редактировать комментарий");
        }

        if (dto.getText() != null && !dto.getText().isBlank()) {
            comment.setText(dto.getText());
        } else {
            throw new ValidationException("Текст комментария не может быть пустым");
        }
        comment.setStatus(CommentStatus.PENDING);
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto deleteComment(Long eventId, Long commentId, Long userId) {
        log.info("Удаления комментария commentId={} по: userId={}, eventId={}", commentId, userId, eventId);

        User user = findUserOrThrow(userId);

        Event event = findEventOrThrow(eventId);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Нельзя удалять комментарии к неопубликованному событию");
        }

        Comment comment = findCommentOrThrow(commentId);

        if (!(commentBelongsToEvent(comment, eventId))) {
            throw new ConflictException("Комментарий не принадлежит указанному событию");
        }

        if (!(userIsOwner(comment, userId))) {
            throw new ConflictException("Только автор может удалять комментарий");
        }
        comment.setStatus(CommentStatus.CANCELED);

        Comment canceledComment = commentRepository.save(comment);

        return commentMapper.toDto(canceledComment);
    }


    @Override
    public CommentDto getMyComment(Long eventId, Long userId) {
        log.info("Получение комментария пользователя по: userId={}, eventId={}", userId, eventId);

        Event event = findEventOrThrow(eventId);
        User user = findUserOrThrow(userId);

        Comment comment = commentRepository.findByCommentatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Комментарий пользователя " + userId + " к событию " + eventId + " не найден"
                ));

        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentShortDto> getCommentsByEvent(Long eventId, int from, int size) {
        log.info("Получения списка комментариев по: eventId={}", eventId);

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Comment> commentPage = commentRepository.findByEventIdAndStatus(
                pageable,
                eventId,
                CommentStatus.PUBLISHED);

        return commentPage.get()
                .map(commentMapper::toShortDto)
                .toList();
    }

    @Override
    public CommentDto getComment(Long commentId) {
        log.info("Получения комментария по: commentId={}", commentId);

        Comment comment = findCommentOrThrow(commentId);

        if (comment.getStatus() != CommentStatus.PUBLISHED) {
            throw new NotFoundException("Комментарий не найден или не опубликован");
        }

        return commentMapper.toDto(comment);
    }


    @Override
    @Transactional
    public CommentDto publishComment(Long commentId) {
        log.info("Публикация комментария администратором: commentId={}", commentId);

        Comment comment = findCommentOrThrow(commentId);

        Event event = comment.getEvent();
        if (event.getState() != EventStatus.PUBLISHED) {
            throw new ConflictException(
                    String.format("Нельзя опубликовать комментарий к событию %d, так как оно не опубликовано", event.getId())
            );
        }

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Комментарий уже удален пользователем, отменен администратором или опубликован");
        }
        comment.setStatus(CommentStatus.PUBLISHED);
        Comment publishComment = commentRepository.save(comment);

        log.info("Комментарий {} успешно опубликован", commentId);

        return commentMapper.toDto(publishComment);
    }

    @Override
    @Transactional
    public CommentDto rejectComment(Long commentId) {
        log.info("Отклонение комментария администратором: commentId={}", commentId);

        Comment comment = findCommentOrThrow(commentId);

        Event event = comment.getEvent();
        if (event.getState() != EventStatus.PUBLISHED) {
            throw new ConflictException(
                    String.format("Нельзя опубликовать комментарий к событию %d, так как оно не опубликовано", event.getId())
            );
        }

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Комментарий уже удален пользователем, отменен администратором или опубликован");
        }
        comment.setStatus(CommentStatus.REJECTED);
        Comment publishComment = commentRepository.save(comment);

        log.info("Комментарий {} успешно отклонен", commentId);

        return commentMapper.toDto(publishComment);
    }

    @Override
    public List<CommentDto> getPendingComments(int from, int size) {
        log.info("Получение комментариев на модерации: from={}, size={}", from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Comment> commentPage = commentRepository.findByStatus(
                pageable,
                CommentStatus.PENDING);

        return commentPage.get()
                .map(commentMapper::toDto)
                .toList();
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Событие не найдено: id={}", id);
                    return new NotFoundException("Событие с ID " + id + " не найдено");
                });
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: id={}", userId);
                    return new NotFoundException("Пользователь с ID " + userId + " не найден");
                });
    }

    private Comment findCommentOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Комментарий не найден: id={}", id);
                    return new NotFoundException("Комментарий с ID " + id + " не найден");
                });
    }

    private boolean userIsOwner(Comment comment, Long userId) {
        return comment.getCommentator().getId().equals(userId);
    }

    private boolean commentBelongsToEvent(Comment comment, Long eventId) {
        return comment.getEvent().getId().equals(eventId);
    }
}
