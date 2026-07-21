package ru.practicum.service;

import jakarta.transaction.Transactional;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    @Transactional
    UserDto createUser(NewUserRequest userDto);

    @Transactional
    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}
