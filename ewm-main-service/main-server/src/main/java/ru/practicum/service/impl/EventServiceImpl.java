package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        log.info("Приватный запрос на получение событий по userId - {}, from - {}, size-{}", userId, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> eventPage = eventRepository.findByUserId(userId, pageable);
        if (!(eventPage.hasContent())) {
            return List.of();
        }
        return eventMapper.toShortDtoList(eventPage.getContent());
    }

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        log.info("Приватный запрос на создание пользователя, userId={}, newEventDto={}", userId, newEventDto);
        User initiator = findUserOrThrow(userId);

        Category category = findCategoryOrThrow(newEventDto.getCategory());

        Event event = eventMapper.toEntityFromNew(newEventDto);

        event.setInitiator(initiator);
        event.setCategory(category);

        if (newEventDto.getEventDate() != null) {
            validateEventDate(newEventDto.getEventDate());
        }

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.info("Запрос на получение события по userId={}, eventId={}", userId, eventId);
        if (userId == null) {
            throw new ValidationException("userId не может быть null");
        }
        if (eventId == null) {
            throw new ValidationException("eventId не может быть null");
        }

        Event event = findEventOrThrow(eventId);

        userIsNotInitiator(event, userId);

        EventFullDto eventDto = eventMapper.toFullDto(event);

        log.info("Событие найдено: id={}, title={}, userId={}",
                eventId, event.getTitle(), userId);

        return eventDto;
    }

    @Override
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        log.info("Запрос на обновление события по userId={}, eventId={}, eventDto={}",
                userId, eventId, eventDto);

        if (userId == null) {
            throw new ValidationException("userId не может быть null");
        }
        if (eventId == null) {
            throw new ValidationException("eventId не может быть null");
        }
        if (eventDto == null) {
            throw new ValidationException("eventDto не может быть null");
        }

        Event event = findEventOrThrow(eventId);

        userIsNotInitiator(event, userId);

        if (event.getState() == EventStatus.PUBLISHED) {
            log.warn("Попытка обновления опубликованного события: eventId={}", eventId);
            throw new ValidationException("Нельзя редактировать опубликованное событие");
        }

        if (eventDto.getEventDate() != null) {
            validateEventDate(eventDto.getEventDate());
        }

        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория с ID " + eventDto.getCategory() + " не найдена"
                    ));
            event.setCategory(category);
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    if (event.getState() == EventStatus.PENDING) {
                        throw new ValidationException("Событие уже на модерации");
                    }
                    if (event.getState() == EventStatus.CANCELED) {
                        throw new ValidationException("Нельзя отправить отмененное событие на модерацию");
                    }
                    event.setState(EventStatus.PENDING);
                    log.info("Событие отправлено на модерацию: eventId={}", eventId);
                    break;

                case CANCEL_REVIEW:
                    if (event.getState() != EventStatus.PENDING) {
                        throw new ValidationException("Событие не на модерации");
                    }
                    event.setState(EventStatus.CANCELED);
                    log.info("Модерация события отменена: eventId={}", eventId);
                    break;

                default:
                    throw new ValidationException("Некорректное действие: " + eventDto.getStateAction());
            }
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие обновлено: eventId={}, userId={}", eventId, userId);

        return eventMapper.toFullDto(updatedEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestEvent(Long userId, Long eventId) {
        log.info("Получение информации о запросах на участие в событии текущего пользователя" +
                " по userId={}, eventId={}", userId, eventId);

        if (userId == null) {
            throw new ValidationException("userId не может быть null");
        }
        if (eventId == null) {
            throw new ValidationException("eventId не может быть null");
        }

        Event event = findEventOrThrow(eventId);

        User user = findUserOrThrow(userId);

        userIsNotInitiator(event, userId);

        List<Request> requests = requestRepository.findByEventId(eventId);
        log.info("Найдено {} запросов на участие в событии: eventId={}, eventTitle={}",
                requests.size(), eventId, event.getTitle());

        return requestMapper.toDtoList(requests);
    }

    @Override
    public EventRequestStatusUpdateResult patchRequestEvent(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest updateRequest) {
        log.info("Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя" +
                " по userId={}, eventId={}", userId, eventId);

        if (userId == null || eventId == null || updateRequest == null) {
            throw new ValidationException("Параметры не могут быть null");
        }

        if (updateRequest.getRequestIds() == null || updateRequest.getRequestIds().isEmpty()) {
            throw new ValidationException("Список ID запросов не может быть пустым");
        }

        Event event = findEventOrThrow(eventId);

        User user = findUserOrThrow(userId);

        userIsNotInitiator(event, userId);

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new ValidationException("Нельзя изменять статус запросов для неопубликованного события");
        }

        int currentConfirmed = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();

        if (participantLimit > 0 && currentConfirmed >= participantLimit) {
            throw new ConflictException("Лимит участников исчерпан");
        }

        List<Request> pendingRequests = requestRepository.findPendingRequestsByIds(
                updateRequest.getRequestIds()
        );

        if (pendingRequests.size() != updateRequest.getRequestIds().size()) {
            throw new ConflictException("Некоторые запросы не найдены или не имеют статуса PENDING");
        }

        for (Request request : pendingRequests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ValidationException("Запрос ID=" + request.getId() +
                        " не принадлежит событию ID=" + eventId);
            }
        }

        for (Request request : pendingRequests) {
            if (request.getRequester().getId().equals(userId)) {
                throw new ConflictException("Инициатор не может подтверждать/отклонять свою заявку");
            }
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
            int availableSlots = participantLimit > 0 ? participantLimit - currentConfirmed : Integer.MAX_VALUE;

            for (Request request : pendingRequests) {
                if (availableSlots > 0) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    availableSlots--;
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }

            if (participantLimit > 0 && event.getConfirmedRequests() >= participantLimit) {
                List<Request> remainingPending = requestRepository.findByEventIdAndStatus(
                        eventId, RequestStatus.PENDING
                );
                for (Request request : remainingPending) {
                    if (!confirmedRequests.contains(request) && !rejectedRequests.contains(request)) {
                        request.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(request);
                    }
                }
            }

        } else if (updateRequest.getStatus() == RequestStatus.REJECTED) {
            for (Request request : pendingRequests) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        } else {
            throw new ValidationException("Статус должен быть CONFIRMED или REJECTED");
        }

        if (!confirmedRequests.isEmpty()) {
            requestRepository.saveAll(confirmedRequests);
        }
        if (!rejectedRequests.isEmpty()) {
            requestRepository.saveAll(rejectedRequests);
        }
        eventRepository.save(event);

        log.info("Статусы запросов обновлены: confirmed={}, rejected={}",
                confirmedRequests.size(), rejectedRequests.size());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestMapper.toDtoList(confirmedRequests))
                .rejectedRequests(requestMapper.toDtoList(rejectedRequests))
                .build();
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<User> users,
                                             List<String> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from,
                                             Integer size) {
        return List.of();
    }

    @Override
    public UpdateEventUserRequest patchAdminEvent(Long eventId) {
        return null;
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size) {
        return List.of();
    }

    @Override
    public EventFullDto getPublicEvent(Long id) {
        return null;
    }

    private void userIsNotInitiator(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("Попытка доступа к событию не инициатором: userId={}, eventId={}, initiatorId={}",
                    userId, event.getId(), event.getInitiator().getId());
            throw new NotFoundException(
                    String.format("Событие с id=%d не найдено или недоступно для пользователя id=%d",
                            event.getId(), userId)
            );
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate == null) {
            throw new ValidationException("Дата события не может быть null");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minEventDate = now.plusHours(2);

        if (eventDate.isBefore(minEventDate)) {
            String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedMin = minEventDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            throw new ConflictException(
                    String.format(
                            "Дата и время события должны быть не раньше чем через 2 часа от текущего момента. " +
                                    "Текущее время: %s, минимальная дата: %s",
                            formattedNow, formattedMin
                    )
            );
        }
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


    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Категория не найдена: id={}", categoryId);
                    return new NotFoundException("Категория с ID " + categoryId + " не найдена");
                });
    }
}
