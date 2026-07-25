package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.model.Compilation;


import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EventMapper.class}
)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDtoList(List<Compilation> compilations);
}