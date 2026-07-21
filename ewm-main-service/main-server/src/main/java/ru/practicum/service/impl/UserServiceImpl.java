package ru.practicum.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<User> userPage = userRepository.findAllByIds(ids, pageable);

        return userMapper.toDtoList(userPage.getContent());

    }


    @Override
    public UserDto createUser(NewUserRequest newUser) {
        log.debug("Создание нового пользователя: {}", newUser);

        if (userRepository.existsByEmail(newUser.getEmail())) {
            log.warn("Попытка создать пользователя с существующим email: {}", newUser.getEmail());
            throw new ConflictException("Пользователь с email " + newUser.getEmail() + " уже существует");
        }

        try {
            User user = userMapper.toUserFromRequest(newUser);
            validateUser(user);
            User savedUser = userRepository.save(user);

            log.info("Пользователь создан: id={}, email={}", savedUser.getId(), savedUser.getEmail());
            return userMapper.toDto(savedUser);

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных: {}", e.getMessage());
            throw new ConflictException("Ошибка при создании пользователя. Возможно, email уже используется.");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Удаление пользователя с ID: {}", userId);
        User user = findUserOrThrow(userId);

        try {
            userRepository.delete(user);
            log.info("Пользователь удален: id={}, email={}", userId, user.getEmail());
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при удалении пользователя: {}", e.getMessage());
            throw new ConflictException("Невозможно удалить пользователя, так как он связан с другими данными.");
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.debug("Получение пользователя по ID: {}", userId);
        User user = findUserOrThrow(userId);
        return userMapper.toDto(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: id={}", userId);
                    return new NotFoundException("Пользователь с ID " + userId + " не найден");
                });
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("Данные пользователя не могут быть null");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }

        if (user.getName().length() < 2) {
            throw new ValidationException("Имя пользователя должно содержать минимум 2 символа");
        }

        if (user.getName().length() > 255) {
            throw new ValidationException("Имя пользователя не может превышать 255 символов");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email пользователя не может быть пустым");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new ValidationException("Некорректный формат email: " + user.getEmail());
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
