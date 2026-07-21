package ru.practicum.service;

import jdk.jfr.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    //private

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    ParticipationRequestDto getRequestEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchRequestEvent(Long userId, Long eventId,
                                                     EventRequestStatusUpdateRequest updateRequest);

    //admin

    List<EventFullDto> getAdminEvents(List<User> users, List<String> states,
                                      List<Long> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Integer from, Integer size);

    UpdateEventUserRequest patchAdminEvent(Long eventId);


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
