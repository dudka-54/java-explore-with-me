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

    // ============================================
    // 1. ENTITY → FULL DTO
    // ============================================
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "createdOn", ignore = true)  // ← Устанавливается в сервисе
    @Mapping(target = "publishedOn", source = "publishedOn")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "views", ignore = true)  // ← Из сервиса статистики
    EventFullDto toFullDto(Event event);

    // ============================================
    // 2. ENTITY → SHORT DTO
    // ============================================
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "views", ignore = true)  // ← Из сервиса статистики
    EventShortDto toShortDto(Event event);

    // ============================================
    // 3. LIST CONVERSIONS
    // ============================================
    List<EventShortDto> toShortDtoList(List<Event> events);
    List<EventFullDto> toFullDtoList(List<Event> events);

    // ============================================
    // 4. NEW EVENT DTO → ENTITY
    // ============================================
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

    // ============================================
    // 5. USER UPDATE DTO → ENTITY
    // ============================================
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

    // ============================================
    // 6. ADMIN UPDATE DTO → ENTITY
    // ============================================
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

    // ============================================
    // 7. DEFAULT METHODS FOR PARTIAL UPDATE
    // ============================================

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
        // stateAction обрабатывается в сервисе
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
        // stateAction обрабатывается в сервисе
    }

    default EventStatus defaultPendingState() {
        return EventStatus.PENDING;
    }
}