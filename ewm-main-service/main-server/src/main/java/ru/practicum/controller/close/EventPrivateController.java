package ru.practicum.controller.close;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<EventShortDto> events = eventService.getEvents(userId, from, size);
        return events.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(events);
    }

    @PostMapping("/events")
    public ResponseEntity<EventFullDto> createUserEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        EventFullDto created = eventService.saveEvent(userId, newEventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        EventFullDto event = eventService.getEvent(userId, eventId);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> updateUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        EventFullDto updated = eventService.patchEvent(userId, eventId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        List<ParticipationRequestDto> requests = eventService.getRequestEvent(userId, eventId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = eventService.patchRequestEvent(userId, eventId, updateRequest);
        return ResponseEntity.ok(result);
    }
}