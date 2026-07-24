package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;


    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.debug("Получение подборок: pinned={}, from={}, size={}", pinned, from, size);

        // 1. Валидация пагинации
        if (from == null) from = 0;
        if (size == null) size = 10;

        if (from < 0) {
            throw new ValidationException("from не может быть отрицательным");
        }
        if (size < 1) {
            throw new ValidationException("size должен быть больше 0");
        }

        // 2. Создание Pageable
        Pageable pageable = PageRequest.of(from / size, size);

        // 3. Получение подборок
        Page<Compilation> compilationPage;

        if (pinned != null) {
            compilationPage = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilationPage = compilationRepository.findAll(pageable);
        }

        // 4. Преобразование в DTO
        List<CompilationDto> compilationDtos = compilationPage.getContent().stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());

        log.info("Найдено {} подборок", compilationDtos.size());
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.debug("Получение подборки по ID: {}", compId);

        if (compId == null) {
            throw new ValidationException("compId не может быть null");
        }

        Compilation compilation = findCompilationOrThrow(compId);
        return compilationMapper.toDto(compilation);
    }

    // ============================================
    // АДМИНСКИЕ МЕТОДЫ
    // ============================================

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.debug("Добавление новой подборки: {}", newCompilationDto);

        // 1. Валидация
        if (newCompilationDto == null) {
            throw new ValidationException("NewCompilationDto не может быть null");
        }

        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new ValidationException("Название подборки не может быть пустым");
        }

        // 2. Проверка на дубликат названия
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle())) {
            throw new ValidationException("Подборка с названием '" + newCompilationDto.getTitle() + "' уже существует");
        }

        // 3. Получение событий (если они есть)
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());

            // Проверка, что все события найдены
            if (events.size() != newCompilationDto.getEvents().size()) {
                throw new ValidationException("Некоторые события не найдены");
            }
        }

        // 4. Создание подборки
        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() != null && newCompilationDto.getPinned())
                .events(events)
                .build();

        // 5. Сохранение
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Подборка создана: id={}, title={}", savedCompilation.getId(), savedCompilation.getTitle());

        return compilationMapper.toDto(savedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.debug("Удаление подборки: id={}", compId);

        if (compId == null) {
            throw new ValidationException("compId не может быть null");
        }

        Compilation compilation = findCompilationOrThrow(compId);
        compilationRepository.delete(compilation);

        log.info("Подборка удалена: id={}, title={}", compId, compilation.getTitle());
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        log.debug("Обновление подборки: id={}, updateData={}", compId, updateCompilationDto);

        // 1. Валидация
        if (compId == null) {
            throw new ValidationException("compId не может быть null");
        }
        if (updateCompilationDto == null) {
            throw new ValidationException("UpdateCompilationDto не может быть null");
        }

        // 2. Поиск подборки
        Compilation compilation = findCompilationOrThrow(compId);

        // 3. Проверка названия на дубликат (если название изменяется)
        if (updateCompilationDto.getTitle() != null) {
            if (compilationRepository.existsByTitleAndIdNot(updateCompilationDto.getTitle(), compId)) {
                throw new ValidationException("Подборка с названием '" + updateCompilationDto.getTitle() + "' уже существует");
            }
            compilation.setTitle(updateCompilationDto.getTitle());
        }

        // 4. Обновление pinned
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        // 5. Обновление списка событий (если передан)
        if (updateCompilationDto.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            if (!updateCompilationDto.getEvents().isEmpty()) {
                events = eventRepository.findAllById(updateCompilationDto.getEvents());

                // Проверка, что все события найдены
                if (events.size() != updateCompilationDto.getEvents().size()) {
                    throw new ValidationException("Некоторые события не найдены");
                }
            }
            compilation.setEvents(events);
        }

        // 6. Сохранение
        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Подборка обновлена: id={}, title={}", updatedCompilation.getId(), updatedCompilation.getTitle());

        return compilationMapper.toDto(updatedCompilation);
    }

    // ============================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ============================================

    private Compilation findCompilationOrThrow(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.warn("Подборка не найдена: id={}", compId);
                    return new NotFoundException("Подборка с ID " + compId + " не найдена");
                });
    }
}

