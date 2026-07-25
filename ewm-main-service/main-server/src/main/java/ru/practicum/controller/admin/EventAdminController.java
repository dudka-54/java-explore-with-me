package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAdminEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<EventFullDto> events = eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
        return events.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(events);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> patchAdminEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest request) {
        EventFullDto updated = eventService.patchAdminEvent(eventId, request);
        return ResponseEntity.ok(updated);
    }
}