package ru.practicum.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.controller.StatsMainSaver;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController implements StatsMainSaver {

    private final UserService userService;
    private final StatClient statClient;
    private final String uri = "/admin/users";

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        saveStat(uri, request);
        return ResponseEntity.ok(userService.getUsers(ids, from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto,
                              HttpServletRequest request) {
        saveStat(uri, request);
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId,
                           HttpServletRequest request) {
        saveStat(uri, request, String.valueOf(userId));
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public StatClient getStatsClient() {
        return statClient;
    }
}

