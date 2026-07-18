package ru.practicum.service;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsersWithParameters(List<Integer> ids, Integer from, Integer size);
}
