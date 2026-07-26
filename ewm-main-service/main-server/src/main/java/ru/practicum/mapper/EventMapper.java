package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.event.*;
import ru.practicum.model.Event;
import ru.practicum.model.EventStatus;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class, UserMapper.class}
)
public interface EventMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", source = "publishedOn")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "views", ignore = true)
    EventFullDto toFullDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(Event event);

    List<EventShortDto> toShortDtoList(List<Event> events);

    List<EventFullDto> toFullDtoList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", expression = "java(defaultPendingState())")
    @Mapping(target = "requestModeration", source = "requestModeration")
    @Mapping(target = "participantLimit", source = "participantLimit")
    @Mapping(target = "paid", source = "paid")
    @Mapping(target = "annotation", source = "annotation")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "eventDate", source = "eventDate")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "title", source = "title")
    Event toEntityFromNew(NewEventDto newEventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "requestModeration", source = "requestModeration")
    @Mapping(target = "participantLimit", source = "participantLimit")
    @Mapping(target = "paid", source = "paid")
    @Mapping(target = "annotation", source = "annotation")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "eventDate", source = "eventDate")
    @Mapping(target = "title", source = "title")
    Event toEntityFromUserUpdate(UpdateEventUserRequest updateRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "requestModeration", source = "requestModeration")
    @Mapping(target = "participantLimit", source = "participantLimit")
    @Mapping(target = "paid", source = "paid")
    @Mapping(target = "annotation", source = "annotation")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "eventDate", source = "eventDate")
    @Mapping(target = "title", source = "title")
    Event toEntityFromAdminUpdate(UpdateEventAdminRequest updateRequest);


    default void updateEventFromUserRequest(UpdateEventUserRequest request, Event event) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }

    default void updateEventFromAdminRequest(UpdateEventAdminRequest request, Event event) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }

    default EventStatus defaultPendingState() {
        return EventStatus.PENDING;
    }
}