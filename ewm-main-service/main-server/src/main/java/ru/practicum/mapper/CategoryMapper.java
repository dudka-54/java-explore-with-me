package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto categoryDto);

    List<CategoryDto> toDtoList(List<Category> categories);


    @Mapping(target = "id", ignore = true)
    Category toCatFromRequest(NewCategoryDto request);
}
