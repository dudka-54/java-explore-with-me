package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);

    List<User> toEntityList(List<UserDto> userDtos);

    @Mapping(target = "id", ignore = true)
    User toUserFromRequest(NewUserRequest request);

    List<UserShortDto> toShortDtoList(List<User> users);

}