package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping("/pending")
    public ResponseEntity<List<CommentDto>> getPendingComments(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getPendingComments(from, size));
    }

    @PatchMapping("/{commentId}/publish")
    public ResponseEntity<CommentDto> publishComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.publishComment(commentId));
    }

    @PatchMapping("/{commentId}/reject")
    public ResponseEntity<CommentDto> rejectComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.rejectComment(commentId));
    }
}