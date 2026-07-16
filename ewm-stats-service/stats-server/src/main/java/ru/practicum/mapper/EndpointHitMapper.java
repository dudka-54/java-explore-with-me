package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Component
public class EndpointHitMapper {
    public static EndpointHit toEntity(EndpointHitDto dto) {
        if (dto == null) {
            return null;
        }

        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}