package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RequestMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "status", source = "status")
    ParticipationRequestDto toDto(Request request);

    List<ParticipationRequestDto> toDtoList(List<Request> requests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    @Mapping(target = "requester", source = "user")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDING")
    Request toEntity(Event event, User user);
}