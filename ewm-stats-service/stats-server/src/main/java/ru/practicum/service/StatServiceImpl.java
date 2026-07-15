package ru.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.EndpointHitDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.ViewStats;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Validated
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Запрос статистики: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        validateDateRange(start, end);

        boolean isUnique = unique != null && unique;
        boolean hasUris = uris != null && !uris.isEmpty();

        List<ViewStats> result;

        if (!hasUris && !isUnique) {
            log.debug("Запрос: Без uris, без unique");
            result = statRepository.getStatsWithoutUrisAndUnique(start, end);

        } else if (!hasUris) {
            log.debug("Запрос: Без uris, с unique");
            result = statRepository.getStatsWithoutUrisWithUnique(start, end);

        } else if (!isUnique) {
            log.debug("Запрос: С uris, без unique");
            result = statRepository.getStatsWithUrisWithoutUnique(start, end, uris);

        } else {
            log.debug("Запрос: С uris, с unique");
            result = statRepository.getStatsWithUrisAndUnique(start, end, uris);
        }

        log.info("Найдено записей: {}", result.size());
        return result;
    }

    @Override
    public void save(@Valid EndpointHitDto dto) {
        if (dto == null) {
            throw new NotFoundException("dto не должен быть null");
        }
        EndpointHit hit = EndpointHitMapper.toEntity(dto);
        statRepository.save(hit);
        log.info("Hit сохранен: app={}, uri={}, ip={}", hit.getApp(), hit.getUri(), hit.getIp());
    }

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new ValidationException("start не может быть null");
        }
        if (end == null) {
            throw new ValidationException("end не может быть null");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("start должен быть раньше end");
        }
        if (start.isEqual(end)) {
            throw new ValidationException("start и end не могут быть равны");
        }
    }
}
