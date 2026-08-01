package ru.practicum.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.PatchCommentDto;


import java.util.List;

public interface CommentService {

    CommentDto addComment(Long eventId, Long userId, NewCommentDto dto);

    CommentDto updateComment(Long eventId, Long commentId, Long userId, PatchCommentDto dto);

    CommentDto deleteComment(Long eventId, Long commentId, Long userId);

    CommentDto getMyComment(Long eventId, Long userId);

    List<CommentShortDto> getCommentsByEvent(Long eventId, int from, int size);

    CommentDto getComment(Long commentId);

    CommentDto publishComment(Long commentId);

    CommentDto rejectComment(Long commentId);

    public List<CommentDto> getPendingComments(int from, int size);
}
