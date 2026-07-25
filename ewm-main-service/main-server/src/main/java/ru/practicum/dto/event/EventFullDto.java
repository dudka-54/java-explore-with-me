package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.EventStatus;
import ru.practicum.model.Location;
import ru.practicum.dto.category.CategoryDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventStatus state;
    private String title;
    private Long views;
}
