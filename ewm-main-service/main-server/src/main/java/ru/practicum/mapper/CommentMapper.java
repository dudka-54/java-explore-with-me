package ru.practicum.mapper;

import org.mapstruct.*;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.PatchCommentDto;
import ru.practicum.model.Comment;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommentMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "commentator", source = "commentator.id")
    @Mapping(target = "status", expression = "java(comment.getStatus().name())")
    CommentDto toDto(Comment comment);

    @Mapping(target = "commentator", source = "commentator.id")
    CommentShortDto toShortDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);

    List<CommentShortDto> toShortDtoList(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "commentator", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "status", ignore = true)
    Comment toEntity(NewCommentDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "commentator", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateComment(@MappingTarget Comment comment, PatchCommentDto dto);
}