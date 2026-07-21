package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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

        validateEventDate(newEventDto.getEventDate());

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.info("Запрос на получение события по userId={}, eventId={}", userId, eventId);

    }

    @Override
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        return null;
    }

    @Override
    public ParticipationRequestDto getRequestEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventRequestStatusUpdateResult patchRequestEvent(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest updateRequest) {
        return null;
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

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate == null) {
            throw new ValidationException("Дата события не может быть null");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minEventDate = now.plusHours(2);

        if (eventDate.isBefore(minEventDate)) {
            String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedMin = minEventDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            throw new ValidationException(
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
