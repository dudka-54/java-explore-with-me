package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Создание запроса на участие: userId={}, eventId={}", userId, eventId);

        User user = findUserOrThrow(userId);

        Event event = findEventOrThrow(eventId);

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new ValidationException("Нельзя подать заявку на неопубликованное событие");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может подать заявку на свое событие");
        }

        if (event.getParticipantLimit() > 0 &&
                event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников исчерпан");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Вы уже подали заявку на это событие");
        }

        Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        Request savedRequest = requestRepository.save(request);
        eventRepository.save(event);

        log.info("Запрос создан: userId={}, eventId={}, status={}",
                userId, eventId, request.getStatus());

        return requestMapper.toDto(savedRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отмена запроса: userId={}, requestId={}", userId, requestId);

        Request request = findRequestOrThrow(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException("Вы можете отменить только свои запросы");
        }

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            throw new ValidationException("Нельзя отменить подтвержденный запрос");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);

        log.info("Запрос отменен: userId={}, requestId={}", userId, requestId);

        return requestMapper.toDto(canceledRequest);
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Получение запросов пользователя: userId={}", userId);

        findUserOrThrow(userId);

        List<Request> requests = requestRepository.findByRequesterId(userId);
        log.info("Найдено {} запросов пользователя", requests.size());

        return requestMapper.toDtoList(requests);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: id={}", userId);
                    return new NotFoundException("Пользователь с ID " + userId + " не найден");
                });
    }

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие не найдено: id={}", eventId);
                    return new NotFoundException("Событие с ID " + eventId + " не найдено");
                });
    }

    private Request findRequestOrThrow(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос не найден: id={}", requestId);
                    return new NotFoundException("Запрос с ID " + requestId + " не найден");
                });
    }
}
