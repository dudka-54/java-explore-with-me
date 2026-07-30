package ru.practicum.controller.close;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping("/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        List<ParticipationRequestDto> requests = requestService.getUserRequests(userId);
        return requests.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(requests);
    }

    @PostMapping("/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        ParticipationRequestDto created = requestService.createRequest(userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        ParticipationRequestDto canceled = requestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(canceled);
    }
}