package ru.practicum.dto.event;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
