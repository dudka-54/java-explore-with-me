package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "annotation не должен быть пуст")
    private String annotation;

    @NotNull(message = "category не должен быть null")
    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "eventDate не должен быть null")
    private LocalDateTime eventDate;

    private Long id;

    @NotNull(message = "initiator не должен быть null")
    private UserShortDto initiator;

    @NotNull(message = "location не долен быть null")
    private Location location;

    @NotNull(message = "paid не долен быть null")
    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventStatus state;

    @NotNull(message = "title не долен быть null")
    private String title;

    private Long views;
}
