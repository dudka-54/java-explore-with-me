package ru.practicum.service;

import jakarta.transaction.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface EventService {
    //private

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    List<ParticipationRequestDto> getRequestEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchRequestEvent(Long userId, Long eventId,
                                                     EventRequestStatusUpdateRequest updateRequest);

    //admin

    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states,
                                      List<Long> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Integer from, Integer size);

    public EventFullDto patchAdminEvent(Long eventId, UpdateEventAdminRequest request);

    //public

    List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size
    );

    EventFullDto getPublicEvent(Long id);
}
