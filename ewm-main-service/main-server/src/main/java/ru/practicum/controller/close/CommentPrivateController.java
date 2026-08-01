package ru.practicum.controller.close;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.PatchCommentDto;
import ru.practicum.service.CommentService;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(eventId, userId, dto));
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @Valid @RequestBody PatchCommentDto dto) {
        return ResponseEntity.ok(commentService.updateComment(eventId, commentId, userId, dto));
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(eventId, commentId, userId));
    }

    @GetMapping("/events/{eventId}/comments")
    public ResponseEntity<CommentDto> getMyComment(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return ResponseEntity.ok(commentService.getMyComment(eventId, userId));
    }
}